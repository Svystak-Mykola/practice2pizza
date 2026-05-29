package pizzeria.infrastructure.persistence.impl;

import pizzeria.domain.entities.Ingredient;
import pizzeria.infrastructure.persistence.contract.IngredientRepository;
import pizzeria.util.ConnectionPool;

import java.sql.*;
import java.util.*;

public class IngredientRepositoryImpl implements IngredientRepository {

  @Override
  public List<Ingredient> findAll() {
    List<Ingredient> list = new ArrayList<>();
    try (Connection conn = ConnectionPool.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM ingredients ORDER BY name")) {
      while (rs.next()) list.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException("Error fetching ingredients", e);
    }
    return list;
  }

  @Override
  public Optional<Ingredient> findById(UUID id) {
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ingredients WHERE id = ?")) {
      stmt.setString(1, id.toString());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) return Optional.of(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException("Error finding ingredient", e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Ingredient> findByName(String name) {
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ingredients WHERE name = ?")) {
      stmt.setString(1, name);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) return Optional.of(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException("Error finding ingredient by name", e);
    }
    return Optional.empty();
  }

  @Override
  public Ingredient save(String name) {
    return findByName(name).orElseGet(() -> {
      String sql = "INSERT INTO ingredients (id, name) VALUES (?, ?) ON CONFLICT(name) DO UPDATE SET name = excluded.name RETURNING *";
      try (Connection conn = ConnectionPool.getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, UUID.randomUUID().toString());
        stmt.setString(2, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return mapRow(rs);
        throw new RuntimeException("Failed to save ingredient");
      } catch (SQLException e) {
        throw new RuntimeException("Error saving ingredient", e);
      }
    });
  }

  @Override
  public void delete(UUID id) {
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM ingredients WHERE id = ?")) {
      stmt.setString(1, id.toString());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting ingredient", e);
    }
  }

  @Override
  public List<Ingredient> findByPizzaId(UUID pizzaId) {
    List<Ingredient> list = new ArrayList<>();
    String sql = "SELECT i.* FROM ingredients i JOIN pizza_ingredients pi ON i.id = pi.ingredient_id WHERE pi.pizza_id = ? ORDER BY i.name";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, pizzaId.toString());
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) list.add(mapRow(rs));
    } catch (SQLException e) {
      throw new RuntimeException("Error finding ingredients for pizza", e);
    }
    return list;
  }

  @Override
  public void setPizzaIngredients(UUID pizzaId, List<Ingredient> ingredients) {
    try (Connection conn = ConnectionPool.getConnection()) {
      conn.setAutoCommit(false);
      try (PreparedStatement del = conn.prepareStatement("DELETE FROM pizza_ingredients WHERE pizza_id = ?")) {
        del.setString(1, pizzaId.toString());
        del.executeUpdate();
      }
      try (PreparedStatement ins = conn.prepareStatement("INSERT INTO pizza_ingredients (pizza_id, ingredient_id) VALUES (?, ?) ON CONFLICT DO NOTHING")) {
        ins.setString(1, pizzaId.toString());
        for (Ingredient ing : ingredients) {
          ins.setString(2, ing.getId().toString());
          ins.addBatch();
        }
        ins.executeBatch();
      }
      conn.setAutoCommit(true);
    } catch (SQLException e) {
      throw new RuntimeException("Error setting pizza ingredients", e);
    }
  }

  private Ingredient mapRow(ResultSet rs) throws SQLException {
    return new Ingredient(UUID.fromString(rs.getString("id")), rs.getString("name"));
  }
}
