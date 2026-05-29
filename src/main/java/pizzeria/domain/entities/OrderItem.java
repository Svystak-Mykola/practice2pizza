package pizzeria.domain.entities;

import pizzeria.domain.enums.PizzaSize;
import java.util.UUID;

public class OrderItem {
  private UUID id;
  private UUID orderId;
  private UUID pizzaId;
  private String pizzaName;
  private String pizzaIngredients;
  private PizzaSize size;
  private int quantity;
  private double priceAtTime;

  public OrderItem() {
    this.size = PizzaSize.M;
  }

  public OrderItem(UUID id, UUID orderId, UUID pizzaId, PizzaSize size, int quantity, double priceAtTime) {
    this(id, orderId, pizzaId, null, null, size, quantity, priceAtTime);
  }

  public OrderItem(UUID id, UUID orderId, UUID pizzaId, String pizzaName, PizzaSize size, int quantity, double priceAtTime) {
    this(id, orderId, pizzaId, pizzaName, null, size, quantity, priceAtTime);
  }

  public OrderItem(UUID id, UUID orderId, UUID pizzaId, String pizzaName, String pizzaIngredients, PizzaSize size, int quantity, double priceAtTime) {
    this.id = id;
    this.orderId = orderId;
    this.pizzaId = pizzaId;
    this.pizzaName = pizzaName;
    this.pizzaIngredients = pizzaIngredients;
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
  public String getPizzaName() { return pizzaName; }
  public void setPizzaName(String pizzaName) { this.pizzaName = pizzaName; }
  public String getPizzaIngredients() { return pizzaIngredients; }
  public void setPizzaIngredients(String pizzaIngredients) { this.pizzaIngredients = pizzaIngredients; }
  public PizzaSize getSize() { return size; }
  public void setSize(PizzaSize size) { this.size = size; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
  public double getPriceAtTime() { return priceAtTime; }
  public void setPriceAtTime(double priceAtTime) { this.priceAtTime = priceAtTime; }

  public String getSizeName() { return size == null ? "M" : size.name(); }
  public void setSizeName(String size) { this.size = PizzaSize.fromString(size); }
}
