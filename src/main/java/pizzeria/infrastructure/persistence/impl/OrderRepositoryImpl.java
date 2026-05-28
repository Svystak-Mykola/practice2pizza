package pizzeria.infrastructure.persistence.impl;

import pizzeria.domain.entities.Order;
import pizzeria.domain.enums.OrderStatus;
import pizzeria.domain.enums.OrderType;
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
    String sql = """
        INSERT INTO orders (id, user_id, created_at, total_amount, status, table_number, order_type,
                            delivery_address, phone, comment)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, order.getId().toString());
      stmt.setString(2, order.getUserId().toString());
      stmt.setTimestamp(3, Timestamp.valueOf(order.getCreatedAt()));
      stmt.setDouble(4, order.getTotalAmount());
      stmt.setString(5, order.getStatusName());

      if (order.getTableNumber() != null) {
        stmt.setInt(6, order.getTableNumber());
      } else {
        stmt.setNull(6, Types.INTEGER);
      }

      stmt.setString(7, order.getOrderTypeName());
      stmt.setString(8, order.getDeliveryAddress());
      stmt.setString(9, order.getPhone());
      stmt.setString(10, order.getComment());

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

  @Override
  public List<Order> findByUserId(UUID userId) {
    List<Order> orders = new ArrayList<>();
    String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, userId.toString());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          orders.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при отриманні замовлень користувача", e);
    }
    return orders;
  }

  @Override
  public void delete(UUID id) {
    String sql = "DELETE FROM orders WHERE id = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, id.toString());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при видаленні замовлення", e);
    }
  }

  @Override
  public List<Order> findByStatus(OrderStatus status) {
    List<Order> orders = new ArrayList<>();
    String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at ASC";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, status.name());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          orders.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при отриманні замовлень за статусом", e);
    }
    return orders;
  }

  @Override
  public void updateStatus(UUID id, OrderStatus status) {
    String sql = "UPDATE orders SET status = ? WHERE id = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, status.name());
      stmt.setString(2, id.toString());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при оновленні статусу замовлення", e);
    }
  }

  private Order mapRow(ResultSet rs) throws SQLException {
    int val = rs.getInt("table_number");
    Integer tableNumber = rs.wasNull() ? null : val;

    return new Order(
        UUID.fromString(rs.getString("id")),
        UUID.fromString(rs.getString("user_id")),
        rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getDouble("total_amount"),
        OrderStatus.fromString(rs.getString("status")),
        tableNumber,
        OrderType.fromString(rs.getString("order_type")),
        readOptionalString(rs, "delivery_address"),
        readOptionalString(rs, "phone"),
        readOptionalString(rs, "comment")
    );
  }

  private String readOptionalString(ResultSet rs, String columnName) throws SQLException {
    try {
      return rs.getString(columnName);
    } catch (SQLException ignored) {
      return null;
    }
  }
}
