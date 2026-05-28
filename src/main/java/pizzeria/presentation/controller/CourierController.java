package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pizzeria.domain.entities.Order;
import pizzeria.domain.enums.OrderStatus;
import pizzeria.domain.enums.OrderType;
import pizzeria.infrastructure.persistence.impl.OrderRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class CourierController {
  @FXML private VBox ordersBox;
  @FXML private Label subtitleLabel;

  private static CourierController instance;
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
    orders.addAll(orderRepo.findByStatus(OrderStatus.READY).stream()
        .filter(order -> OrderType.DELIVERY == order.getOrderType())
        .toList());
    orders.addAll(orderRepo.findByStatus(OrderStatus.ON_THE_WAY).stream()
        .filter(order -> OrderType.DELIVERY == order.getOrderType())
        .toList());
    subtitleLabel.setText(orders.size() + " замовлень для доставки");
    for (Order order : orders) {
      Button delivered = action("ДОСТАВЛЕНО", OrderStatus.DELIVERED, order);
      if (order.getStatus() == OrderStatus.ON_THE_WAY) {
        ordersBox.getChildren().add(DashboardCardFactory.orderCard(order, delivered));
      } else {
        Button way = action("В ДОРОЗІ", OrderStatus.ON_THE_WAY, order);
        ordersBox.getChildren().add(DashboardCardFactory.orderCard(order, way, delivered));
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
      AdminController.refreshDashboard();
    });
    return button;
  }
}
