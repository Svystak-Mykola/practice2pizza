package pizzeria.infrastructure.persistence.impl;

import pizzeria.util.ConnectionPool;
import pizzeria.domain.entities.Pizza;
import pizzeria.infrastructure.persistence.contract.PizzaRepository;

import java.sql.*;
import java.util.*;

public class PizzaRepositoryImpl implements PizzaRepository {

  @Override
  public List<Pizza> findAll() {
    List<Pizza> pizzas = new ArrayList<>();
    String sql = "SELECT p.id, p.category_id, p.name, p.price, c.name as category_name " +
        "FROM pizzas p LEFT JOIN categories c ON p.category_id = c.id";

    try (Connection conn = ConnectionPool.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        pizzas.add(mapRow(rs));
      }

    } catch (SQLException e) {
      throw new RuntimeException("Error fetching pizzas", e);
    }

    return pizzas;
  }

  @Override
  public void save(Pizza pizza) {
    String sql = "INSERT INTO pizzas (id, category_id, name, price) VALUES (?, ?, ?, ?)";

    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, pizza.getId().toString());
      stmt.setString(2, pizza.getCategoryId().toString());
      stmt.setString(3, pizza.getName());
      stmt.setDouble(4, pizza.getPrice());

      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Error saving pizza", e);
    }
  }

  @Override
  public Optional<Pizza> findById(UUID id) {
    String sql = "SELECT p.id, p.category_id, p.name, p.price, c.name as category_name " +
        "FROM pizzas p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";

    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, id.toString());
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return Optional.of(mapRow(rs));
      }

    } catch (SQLException e) {
      throw new RuntimeException("Error finding pizza by id", e);
    }

    return Optional.empty();
  }

  @Override
  public void delete(UUID id) {
    String sql = "DELETE FROM pizzas WHERE id = ?";

    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, id.toString());
      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Error deleting pizza", e);
    }
  }

  private Pizza mapRow(ResultSet rs) throws SQLException {
    Pizza pizza = new Pizza(
        UUID.fromString(rs.getString("id")),
        UUID.fromString(rs.getString("category_id")),
        rs.getString("category_name"),
        rs.getString("name"),
        rs.getDouble("price")
    );
    return pizza;
  }
}
