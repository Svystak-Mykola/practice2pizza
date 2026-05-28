package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
  List<Category> findAll();
  Optional<Category> findById(UUID id);
  Optional<Category> findByName(String name);
}
