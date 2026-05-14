package pizzeria.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order {
  private UUID id;
  private UUID userId;
  private LocalDateTime createdAt;
  private double totalAmount;
  private String status;
  private Integer tableNumber;
  private String orderType;

  public Order() {}

  public Order(UUID id, UUID userId, LocalDateTime createdAt, double totalAmount,
               String status, Integer tableNumber, String orderType) {
    this.id = id;
    this.userId = userId;
    this.createdAt = createdAt;
    this.totalAmount = totalAmount;
    this.status = status;
    this.tableNumber = tableNumber;
    this.orderType = orderType;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public double getTotalAmount() { return totalAmount; }
  public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Integer getTableNumber() { return tableNumber; }
  public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }
  public String getOrderType() { return orderType; }
  public void setOrderType(String orderType) { this.orderType = orderType; }
}
