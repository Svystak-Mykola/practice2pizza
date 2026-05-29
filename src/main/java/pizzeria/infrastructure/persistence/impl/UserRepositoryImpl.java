package pizzeria.infrastructure.persistence.impl;

import pizzeria.domain.entities.User;
import pizzeria.domain.enums.Role;
import pizzeria.infrastructure.persistence.contract.UserRepository;
import pizzeria.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

  @Override
  public List<User> findAll() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users ORDER BY name";
    try (Connection conn = ConnectionPool.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        users.add(mapUser(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при отриманні списку користувачів", e);
    }
    return users;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return Optional.of(mapUser(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при пошуку користувача за email: " + e.getMessage());
    }
    return Optional.empty();
  }

  @Override
  public void save(User user) {
    String sql = """
        INSERT INTO users (id, name, email, password, avatar_path, phone, role)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(id) DO UPDATE SET
          name = excluded.name,
          email = excluded.email,
          password = excluded.password,
          avatar_path = excluded.avatar_path,
          phone = excluded.phone,
          role = excluded.role
        """;
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, user.getId().toString());
      stmt.setString(2, user.getName());
      stmt.setString(3, user.getEmail());
      stmt.setString(4, user.getPassword());
      stmt.setString(5, user.getAvatarPath());
      stmt.setString(6, user.getPhone());
      stmt.setString(7, user.getRoleName());

      stmt.executeUpdate();
      System.out.println("Юзер збережений в БД: " + user.getEmail());
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при збереженні юзера: " + e.getMessage());
    }
  }

  private User mapUser(ResultSet rs) throws SQLException {
    return new User(
        UUID.fromString(rs.getString("id")),
        rs.getString("name"),
        rs.getString("email"),
        rs.getString("password"),
        readOptionalString(rs, "avatar_path"),
        readOptionalString(rs, "phone"),
        Role.fromString(readOptionalString(rs, "role", "USER"))
    );
  }

  private String readOptionalString(ResultSet rs, String columnName) throws SQLException {
    return readOptionalString(rs, columnName, null);
  }

  private String readOptionalString(ResultSet rs, String columnName, String defaultValue) throws SQLException {
    try {
      String value = rs.getString(columnName);
      return value == null ? defaultValue : value;
    } catch (SQLException ignored) {
      return defaultValue;
    }
  }
}
