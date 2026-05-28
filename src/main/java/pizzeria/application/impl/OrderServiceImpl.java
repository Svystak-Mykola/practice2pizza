package pizzeria.application.impl;

import pizzeria.application.contract.OrderService;
import pizzeria.domain.entities.Order;
import pizzeria.domain.entities.OrderItem;
import pizzeria.domain.enums.OrderStatus;
import pizzeria.domain.enums.OrderType;
import pizzeria.infrastructure.persistence.contract.OrderItemRepository;
import pizzeria.infrastructure.persistence.contract.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepo;
  private final OrderItemRepository itemRepo;

  public OrderServiceImpl(OrderRepository orderRepo, OrderItemRepository itemRepo) {
    this.orderRepo = orderRepo;
    this.itemRepo = itemRepo;
  }

  @Override
  public void createOrder(UUID userId, List<OrderItem> cartItems, Integer tableNumber, OrderType orderType) {
    createOrder(userId, cartItems, tableNumber, orderType, null, null, null);
  }

  @Override
  public void createOrder(UUID userId, List<OrderItem> cartItems, Integer tableNumber, OrderType orderType,
                          String deliveryAddress, String phone, String comment) {
    UUID orderId = UUID.randomUUID();

    double total = cartItems.stream()
        .mapToDouble(i -> i.getPriceAtTime() * i.getQuantity())
        .sum();

    Order order = new Order(orderId, userId, LocalDateTime.now(), total, OrderStatus.NEW, tableNumber, orderType,
        deliveryAddress, phone, comment);
    orderRepo.save(order);

    for (OrderItem item : cartItems) {
      item.setOrderId(orderId);
      itemRepo.save(item);
    }
  }
}
