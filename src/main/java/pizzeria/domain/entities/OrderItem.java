package pizzeria.domain.entities;

import pizzeria.domain.enums.PizzaSize;
import java.util.UUID;

public class OrderItem {
  private UUID id;
  private UUID orderId;
  private UUID pizzaId;
  private PizzaSize size;
  private int quantity;
  private double priceAtTime;

  public OrderItem() {
    this.size = PizzaSize.M;
  }

  public OrderItem(UUID id, UUID orderId, UUID pizzaId, PizzaSize size, int quantity, double priceAtTime) {
    this.id = id;
    this.orderId = orderId;
    this.pizzaId = pizzaId;
    this.size = size;
    this.quantity = quantity;
    this.priceAtTime = priceAtTime;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getOrderId() { return orderId; }
  public void setOrderId(UUID orderId) { this.orderId = orderId; }
  public UUID getPizzaId() { return pizzaId; }
  public void setPizzaId(UUID pizzaId) { this.pizzaId = pizzaId; }
  public PizzaSize getSize() { return size; }
  public void setSize(PizzaSize size) { this.size = size; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
  public double getPriceAtTime() { return priceAtTime; }
  public void setPriceAtTime(double priceAtTime) { this.priceAtTime = priceAtTime; }

  public String getSizeName() { return size == null ? "M" : size.name(); }
  public void setSizeName(String size) { this.size = PizzaSize.fromString(size); }
}
