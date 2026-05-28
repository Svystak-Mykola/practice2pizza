package pizzeria.application.impl;

import pizzeria.application.contract.CartService;
import pizzeria.domain.entities.Pizza;
import pizzeria.infrastructure.persistence.impl.PizzaRepositoryImpl;
import pizzeria.infrastructure.session.UserSession;
import pizzeria.util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CartServiceImpl implements CartService {

  private final Map<Pizza, Integer> items = new HashMap<>();
  private final PizzaRepositoryImpl pizzaRepository = new PizzaRepositoryImpl();

  public CartServiceImpl() {
    reloadForCurrentUser();
  }

  @Override
  public void addPizza(Pizza pizza) {
    if (pizza == null) {
      return;
    }
    items.put(pizza, items.getOrDefault(pizza, 0) + 1);
    saveItem(pizza, items.get(pizza));
  }

  @Override
  public void removePizza(Pizza pizza) {
    if (!items.containsKey(pizza)) {
      return;
    }

    int quantity = items.get(pizza);
    if (quantity <= 1) {
      items.remove(pizza);
      deleteItem(pizza);
    } else {
      items.put(pizza, quantity - 1);
      saveItem(pizza, quantity - 1);
    }
  }

  @Override
  public Map<Pizza, Integer> getItems() {
    return items;
  }

  @Override
  public double getTotal() {
    return items.entrySet()
        .stream()
        .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
        .sum();
  }

  @Override
  public void clear() {
    items.clear();
    UUID userId = currentUserId();
    if (userId == null) return;
    String sql = "DELETE FROM cart_items WHERE user_id = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, userId.toString());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося очистити кошик", e);
    }
  }

  public void clearLocalOnly() {
    items.clear();
  }

  public void reloadForCurrentUser() {
    items.clear();
    UUID userId = currentUserId();
    if (userId == null) return;

    String sql = "SELECT pizza_id, quantity FROM cart_items WHERE user_id = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, userId.toString());
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        UUID pizzaId = UUID.fromString(rs.getString("pizza_id"));
        int quantity = rs.getInt("quantity");
        pizzaRepository.findById(pizzaId)
            .filter(pizza -> quantity > 0)
            .ifPresent(pizza -> items.put(pizza, quantity));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося завантажити кошик", e);
    }
  }

  public void persistCurrentCart() {
    for (Map.Entry<Pizza, Integer> entry : items.entrySet()) {
      saveItem(entry.getKey(), entry.getValue());
    }
  }

  private void saveItem(Pizza pizza, int quantity) {
    UUID userId = currentUserId();
    if (userId == null) return;

    String sql = """
        INSERT INTO cart_items (user_id, pizza_id, quantity)
        VALUES (?, ?, ?)
        ON CONFLICT(user_id, pizza_id) DO UPDATE SET
          quantity = excluded.quantity
        """;
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, userId.toString());
      stmt.setString(2, pizza.getId().toString());
      stmt.setInt(3, quantity);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося зберегти кошик", e);
    }
  }

  private void deleteItem(Pizza pizza) {
    UUID userId = currentUserId();
    if (userId == null) return;

    String sql = "DELETE FROM cart_items WHERE user_id = ? AND pizza_id = ?";
    try (Connection conn = ConnectionPool.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, userId.toString());
      stmt.setString(2, pizza.getId().toString());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося оновити кошик", e);
    }
  }

  private UUID currentUserId() {
    return UserSession.getCurrentUser() == null ? null : UserSession.getCurrentUser().getId();
  }
}
