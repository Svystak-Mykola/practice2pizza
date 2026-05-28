package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pizzeria.domain.entities.Order;
import pizzeria.domain.enums.OrderStatus;
import pizzeria.infrastructure.persistence.impl.OrderRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class KitchenController {
  @FXML private VBox ordersBox;
  @FXML private Label subtitleLabel;

  private static KitchenController instance;
  private final OrderRepositoryImpl orderRepo = new OrderRepositoryImpl();

  @FXML
  public void initialize() {
    instance = this;
    load();
  }

  public static void refreshDashboard() {
    if (instance != null) instance.load();
  }

  private void load() {
    ordersBox.getChildren().clear();
    List<Order> orders = new ArrayList<>();
    orders.addAll(orderRepo.findByStatus(OrderStatus.NEW));
    orders.addAll(orderRepo.findByStatus(OrderStatus.IN_PROGRESS));
    subtitleLabel.setText(orders.size() + " замовлень для кухні");
    for (Order order : orders) {
      Button ready = action("ГОТОВО", OrderStatus.READY, order);
      if (order.getStatus() == OrderStatus.IN_PROGRESS) {
        ordersBox.getChildren().add(DashboardCardFactory.orderCard(order, ready));
      } else {
        Button work = action("В роботу", OrderStatus.IN_PROGRESS, order);
        ordersBox.getChildren().add(DashboardCardFactory.orderCard(order, work, ready));
      }
    }
  }

  private Button action(String text, OrderStatus status, Order order) {
    Button button = new Button(text);
    button.getStyleClass().add("checkout-btn");
    button.setOnAction(e -> {
      orderRepo.updateStatus(order.getId(), status);
      load();
      OrdersController.refreshOrders();
      CourierController.refreshDashboard();
      AdminController.refreshDashboard();
    });
    return button;
  }
}
