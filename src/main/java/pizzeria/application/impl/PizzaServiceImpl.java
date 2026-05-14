package pizzeria.application.impl;

import pizzeria.application.contract.PizzaService;
import pizzeria.application.exception.ValidationException;
import pizzeria.domain.entities.Pizza;
import pizzeria.infrastructure.persistence.contract.PizzaRepository;
import pizzeria.infrastructure.persistence.impl.PizzaRepositoryImpl;

import java.util.List;
import java.util.UUID;

public class PizzaServiceImpl implements PizzaService {

  private final PizzaRepository pizzaRepository;

  public PizzaServiceImpl() {
    this.pizzaRepository = new PizzaRepositoryImpl();
  }

  @Override
  public void addNewPizza(String name, double price) {

    if (name == null || name.isBlank()) {
      throw new ValidationException("Назва піци не може бути порожньою");
    }

    if (price <= 0) {
      throw new ValidationException("Ціна має бути більше 0");
    }

    Pizza pizza = new Pizza(
        UUID.randomUUID(),
        UUID.randomUUID(),
        null,
        name,
        price
    );

    pizzaRepository.save(pizza);
  }

  @Override
  public List<Pizza> getAllPizzas() {
    return pizzaRepository.findAll();
  }
}
