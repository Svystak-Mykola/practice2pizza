package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  void save(Order order);

  List<Order> findAll();

  Optional<Order> findById(UUID id);
}
