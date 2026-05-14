package pizzeria.application.contract;

import pizzeria.domain.entities.OrderItem;
import java.util.List;
import java.util.UUID;

public interface OrderService {
  void createOrder(UUID userId, List<OrderItem> cartItems, Integer tableNumber, String orderType);
}
