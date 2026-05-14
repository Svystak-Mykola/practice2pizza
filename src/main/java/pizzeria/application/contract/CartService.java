package pizzeria.application.contract;

import pizzeria.domain.entities.Pizza;

import java.util.Map;

public interface CartService {

  void addPizza(Pizza pizza);

  void removePizza(Pizza pizza);

  Map<Pizza, Integer> getItems();

  double getTotal();

  void clear();
}
