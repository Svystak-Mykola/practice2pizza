package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.Pizza;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PizzaRepository {

  void save(Pizza pizza);

  Optional<Pizza> findById(UUID id);

  List<Pizza> findAll();

  void delete(UUID id);
}
