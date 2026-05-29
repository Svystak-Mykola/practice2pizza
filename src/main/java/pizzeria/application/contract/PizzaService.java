package pizzeria.application.contract;

import pizzeria.domain.entities.Category;
import pizzeria.domain.entities.Ingredient;
import pizzeria.domain.entities.Pizza;
import java.util.List;
import java.util.UUID;

public interface PizzaService {
  void addNewPizza(String name, double price, UUID categoryId, String ingredients);
  void updatePizza(UUID id, String name, double price, UUID categoryId, String ingredients);
  void deletePizza(UUID id);
  List<Pizza> getAllPizzas();
  List<Category> getAllCategories();
  List<Ingredient> getAllIngredients();
  Ingredient saveIngredient(String name);
  void deleteIngredient(UUID id);
}
