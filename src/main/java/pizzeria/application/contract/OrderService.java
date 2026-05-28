package pizzeria.application.contract;

import pizzeria.domain.entities.OrderItem;
import pizzeria.domain.enums.OrderType;
import java.util.List;
import java.util.UUID;

public interface OrderService {
  void createOrder(UUID userId, List<OrderItem> cartItems, Integer tableNumber, OrderType orderType);
  void createOrder(UUID userId, List<OrderItem> cartItems, Integer tableNumber, OrderType orderType,
                   String deliveryAddress, String phone, String comment);
}
