package pizzeria.application.impl;

import pizzeria.application.contract.PizzaService;
import pizzeria.application.exception.ValidationException;
import pizzeria.domain.entities.Category;
import pizzeria.domain.entities.Pizza;
import pizzeria.infrastructure.persistence.contract.CategoryRepository;
import pizzeria.infrastructure.persistence.contract.PizzaRepository;
import pizzeria.infrastructure.persistence.impl.CategoryRepositoryImpl;
import pizzeria.infrastructure.persistence.impl.PizzaRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PizzaServiceImpl implements PizzaService {

  private final PizzaRepository pizzaRepository;
  private final CategoryRepository categoryRepository;

  public PizzaServiceImpl() {
    this.pizzaRepository = new PizzaRepositoryImpl();
    this.categoryRepository = new CategoryRepositoryImpl();
  }

  @Override
  public void addNewPizza(String name, double price, UUID categoryId, String ingredients) {

    if (name == null || name.isBlank()) {
      throw new ValidationException("Назва піци не може бути порожньою");
    }

    if (price <= 0) {
      throw new ValidationException("Ціна має бути більше 0");
    }

    String categoryName = null;
    if (categoryId != null) {
      categoryName = categoryRepository.findById(categoryId)
          .map(Category::getName).orElse(null);
    }
    Pizza pizza = new Pizza(
        UUID.randomUUID(),
        categoryId != null ? categoryId : UUID.randomUUID(),
        categoryName,
        name,
        price,
        ingredients != null ? ingredients : ""
    );

    pizzaRepository.save(pizza);
  }

  @Override
  public void updatePizza(UUID id, String name, double price, UUID categoryId, String ingredients) {
    if (name == null || name.isBlank()) {
      throw new ValidationException("Назва піци не може бути порожньою");
    }
    if (price <= 0) {
      throw new ValidationException("Ціна має бути більше 0");
    }
    String categoryName = null;
    if (categoryId != null) {
      categoryName = categoryRepository.findById(categoryId)
          .map(Category::getName).orElse(null);
    }
    Pizza pizza = new Pizza(
        id,
        categoryId != null ? categoryId : UUID.randomUUID(),
        categoryName,
        name,
        price,
        ingredients != null ? ingredients : ""
    );
    pizzaRepository.save(pizza);
  }

  @Override
  public void deletePizza(UUID id) {
    pizzaRepository.delete(id);
  }

  @Override
  public List<Pizza> getAllPizzas() {
    return pizzaRepository.findAll();
  }

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }
}
