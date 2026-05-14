package pizzeria.application.impl;

import pizzeria.application.contract.OrderService;
import pizzeria.domain.entities.Order;
import pizzeria.domain.entities.OrderItem;
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
  public void createOrder(UUID userId, List<OrderItem> cartItems, Integer tableNumber, String orderType) {
    UUID orderId = UUID.randomUUID();

    double total = cartItems.stream()
        .mapToDouble(i -> i.getPriceAtTime() * i.getQuantity())
        .sum();

    Order order = new Order(orderId, userId, LocalDateTime.now(), total, "ПРИЙНЯТО", tableNumber, orderType);
    orderRepo.save(order);

    for (OrderItem item : cartItems) {
      item.setOrderId(orderId);
      itemRepo.save(item);
    }
  }
}
