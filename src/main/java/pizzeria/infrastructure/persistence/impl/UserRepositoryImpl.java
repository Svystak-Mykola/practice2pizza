package pizzeria.infrastructure.persistence.impl;

import pizzeria.domain.entities.User;
import pizzeria.infrastructure.persistence.contract.UserRepository;
import pizzeria.util.ConnectionPool;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

  @Override
  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        User user = new User(
            UUID.fromString(rs.getString("id")),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            readOptionalString(rs, "avatar_path")
        );
        return Optional.of(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public void save(User user) {
    String sql = """
        INSERT INTO users (id, name, email, password, avatar_path)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT(id) DO UPDATE SET
          name = excluded.name,
          email = excluded.email,
          password = excluded.password,
          avatar_path = excluded.avatar_path
        """;
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, user.getId().toString());
      stmt.setString(2, user.getName());
      stmt.setString(3, user.getEmail());
      stmt.setString(4, user.getPassword());
      stmt.setString(5, user.getAvatarPath());

      stmt.executeUpdate();
      System.out.println("Юзер збережений в БД: " + user.getEmail());
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при збереженні юзера: " + e.getMessage());
    }
  }

  private String readOptionalString(ResultSet rs, String columnName) throws SQLException {
    try {
      return rs.getString(columnName);
    } catch (SQLException ignored) {
      return null;
    }
  }
}
