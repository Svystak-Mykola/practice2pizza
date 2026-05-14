package pizzeria.application.contract;

import pizzeria.domain.entities.Pizza;
import java.util.List;

public interface PizzaService {
  void addNewPizza(String name, double price);
  List<Pizza> getAllPizzas();
}
