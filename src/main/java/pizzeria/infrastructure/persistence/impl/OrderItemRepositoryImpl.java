package pizzeria.infrastructure.persistence.impl;

import pizzeria.domain.entities.OrderItem;
import pizzeria.domain.enums.PizzaSize;
import pizzeria.infrastructure.persistence.contract.OrderItemRepository;
import pizzeria.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderItemRepositoryImpl implements OrderItemRepository {

  @Override
  public void save(OrderItem item) {
    String sql = "INSERT INTO order_items (id, order_id, pizza_id, size, quantity, price_at_time) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, item.getId().toString());
      stmt.setString(2, item.getOrderId().toString());
      stmt.setString(3, item.getPizzaId().toString());
      stmt.setString(4, item.getSizeName());
      stmt.setInt(5, item.getQuantity());
      stmt.setDouble(6, item.getPriceAtTime());

      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при збереженні позиції замовлення", e);
    }
  }

  @Override
  public List<OrderItem> findByOrderId(UUID orderId) {
    List<OrderItem> items = new ArrayList<>();
    String sql = "SELECT oi.*, p.name AS pizza_name, (SELECT STRING_AGG(i.name, ', ') FROM pizza_ingredients pi JOIN ingredients i ON i.id = pi.ingredient_id WHERE pi.pizza_id = p.id) AS pizza_ingredients FROM order_items oi JOIN pizzas p ON p.id = oi.pizza_id WHERE oi.order_id = ?";

    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, orderId.toString());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          items.add(new OrderItem(
              UUID.fromString(rs.getString("id")),
              UUID.fromString(rs.getString("order_id")),
              UUID.fromString(rs.getString("pizza_id")),
              rs.getString("pizza_name"),
              rs.getString("pizza_ingredients"),
              PizzaSize.fromString(rs.getString("size")),
              rs.getInt("quantity"),
              rs.getDouble("price_at_time")
          ));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Помилка при пошуку позицій замовлення", e);
    }
    return items;
  }
}
