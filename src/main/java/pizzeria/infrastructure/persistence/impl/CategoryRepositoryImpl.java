package pizzeria.infrastructure.persistence.impl;

import pizzeria.domain.entities.Category;
import pizzeria.infrastructure.persistence.contract.CategoryRepository;
import pizzeria.util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryRepositoryImpl implements CategoryRepository {

  @Override
  public List<Category> findAll() {
    List<Category> categories = new ArrayList<>();
    String sql = "SELECT id, name FROM categories ORDER BY name";

    try (Connection conn = ConnectionPool.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        categories.add(new Category(
            UUID.fromString(rs.getString("id")),
            rs.getString("name")
        ));
      }
    } catch (Exception e) {
      throw new RuntimeException("Error fetching categories", e);
    }

    return categories;
  }

  @Override
  public Optional<Category> findById(UUID id) {
    String sql = "SELECT id, name FROM categories WHERE id = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, id.toString());
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return Optional.of(new Category(
            UUID.fromString(rs.getString("id")),
            rs.getString("name")
        ));
      }
    } catch (Exception e) {
      throw new RuntimeException("Error finding category by id", e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Category> findByName(String name) {
    String sql = "SELECT id, name FROM categories WHERE name = ?";
    try (Connection conn = ConnectionPool.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return Optional.of(new Category(
            UUID.fromString(rs.getString("id")),
            rs.getString("name")
        ));
      }
    } catch (Exception e) {
      throw new RuntimeException("Error finding category by name", e);
    }
    return Optional.empty();
  }
}
