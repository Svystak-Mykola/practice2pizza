package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.Order;
import pizzeria.domain.enums.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  void save(Order order);

  List<Order> findAll();

  Optional<Order> findById(UUID id);

  List<Order> findByUserId(UUID userId);

  List<Order> findByStatus(OrderStatus status);

  void updateStatus(UUID id, OrderStatus status);

  void delete(UUID id);
}
