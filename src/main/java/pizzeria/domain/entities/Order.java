package pizzeria.domain.entities;

import pizzeria.domain.enums.OrderStatus;
import pizzeria.domain.enums.OrderType;
import java.time.LocalDateTime;
import java.util.UUID;

public class Order {
  private UUID id;
  private UUID userId;
  private LocalDateTime createdAt;
  private double totalAmount;
  private OrderStatus status;
  private Integer tableNumber;
  private OrderType orderType;
  private String deliveryAddress;
  private String phone;
  private String comment;

  public Order() {
    this.status = OrderStatus.NEW;
  }

  public Order(UUID id, UUID userId, LocalDateTime createdAt, double totalAmount,
               OrderStatus status, Integer tableNumber, OrderType orderType) {
    this(id, userId, createdAt, totalAmount, status, tableNumber, orderType, null, null, null);
  }

  public Order(UUID id, UUID userId, LocalDateTime createdAt, double totalAmount,
               OrderStatus status, Integer tableNumber, OrderType orderType,
               String deliveryAddress, String phone, String comment) {
    this.id = id;
    this.userId = userId;
    this.createdAt = createdAt;
    this.totalAmount = totalAmount;
    this.status = status;
    this.tableNumber = tableNumber;
    this.orderType = orderType;
    this.deliveryAddress = deliveryAddress;
    this.phone = phone;
    this.comment = comment;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public double getTotalAmount() { return totalAmount; }
  public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
  public OrderStatus getStatus() { return status; }
  public void setStatus(OrderStatus status) { this.status = status; }
  public Integer getTableNumber() { return tableNumber; }
  public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }
  public OrderType getOrderType() { return orderType; }
  public void setOrderType(OrderType orderType) { this.orderType = orderType; }
  public String getDeliveryAddress() { return deliveryAddress; }
  public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getComment() { return comment; }
  public void setComment(String comment) { this.comment = comment; }

  public String getStatusName() { return status == null ? "NEW" : status.name(); }
  public void setStatusName(String status) { this.status = OrderStatus.fromString(status); }
  public String getOrderTypeName() { return orderType == null ? "DINE_IN" : orderType.name(); }
  public void setOrderTypeName(String orderType) { this.orderType = OrderType.fromString(orderType); }
}
