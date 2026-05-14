package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.OrderItem;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository {
  void save(OrderItem item);
  List<OrderItem> findByOrderId(UUID orderId);
}
