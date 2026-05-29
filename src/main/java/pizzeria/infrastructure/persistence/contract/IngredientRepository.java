package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository {

  List<Ingredient> findAll();

  Optional<Ingredient> findById(UUID id);

  Optional<Ingredient> findByName(String name);

  Ingredient save(String name);

  void delete(UUID id);

  List<Ingredient> findByPizzaId(UUID pizzaId);

  void setPizzaIngredients(UUID pizzaId, List<Ingredient> ingredients);
}
