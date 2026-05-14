package pizzeria.infrastructure.persistence.impl;

import pizzeria.domain.entities.Order;
import pizzeria.infrastructure.persistence.contract.OrderRepository;
import pizzeria.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryImpl implements OrderRepository {

  @Override
  public void save(Order order) {
    String sql = "INSERT INTO orders (id, user_id, created_at, total_amount, status, table_number, order_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, order.getId().toString());
      stmt.setString(2, order.getUserId().toString());
      stmt.setTimestamp(3, Timestamp.valueOf(order.getCreatedAt()));
      stmt.setDouble(4, order.getTotalAmount());
      stmt.setString(5, order.getStatus());

      if (order.getTableNumber() != null) {
        stmt.setInt(6, order.getTableNumber());
      } else {
        stmt.setNull(6, Types.INTEGER);
      }

      stmt.setString(7, order.getOrderType());

      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при збереженні замовлення", e);
    }
  }

  @Override
  public List<Order> findAll() {
    List<Order> orders = new ArrayList<>();
    String sql = "SELECT * FROM orders ORDER BY created_at DESC";

    try (Connection conn = ConnectionPool.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        orders.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при отриманні списку замовлень", e);
    }
    return orders;
  }

  @Override
  public Optional<Order> findById(UUID id) {
    String sql = "SELECT * FROM orders WHERE id = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, id.toString());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при пошуку замовлення", e);
    }
    return Optional.empty();
  }

  private Order mapRow(ResultSet rs) throws SQLException {
    int val = rs.getInt("table_number");
    Integer tableNumber = rs.wasNull() ? null : val;

    return new Order(
        UUID.fromString(rs.getString("id")),
        UUID.fromString(rs.getString("user_id")),
        rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getDouble("total_amount"),
        rs.getString("status"),
        tableNumber,
        rs.getString("order_type")
    );
  }
}
