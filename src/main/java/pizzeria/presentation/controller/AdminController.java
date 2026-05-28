package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pizzeria.domain.entities.Order;
import pizzeria.infrastructure.persistence.impl.OrderRepositoryImpl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminController {
  @FXML private VBox ordersBox;
  @FXML private Label ordersCountLabel;
  @FXML private Label revenueLabel;
  @FXML private Label subtitleLabel;

  private static AdminController instance;
  private final OrderRepositoryImpl orderRepo = new OrderRepositoryImpl();

  static final Map<String, String> customImages = new HashMap<>();
  static final String UPLOAD_DIR = System.getProperty("user.home")
      + File.separator + ".urpizza" + File.separator + "images";

  public static void registerCustomImage(String pizzaName, String filename) {
    customImages.put(pizzaName, filename);
  }

  public static Image loadCustomImage(String pizzaName, double width, double height) {
    String filename = customImages.get(pizzaName);
    if (filename != null) {
      File file = new File(UPLOAD_DIR, filename);
      if (file.exists()) {
        return new Image(file.toURI().toString(), width, height, false, true);
      }
    }
    return null;
  }

  @FXML
  public void initialize() {
    instance = this;
    loadOrders();
  }

  public static void refreshDashboard() {
    if (instance != null) instance.loadOrders();
  }

  private void loadOrders() {
    List<Order> orders = orderRepo.findAll();
    ordersBox.getChildren().clear();
    ordersCountLabel.setText("Замовлень: " + orders.size());
    double total = orders.stream().mapToDouble(Order::getTotalAmount).sum();
    revenueLabel.setText(String.format("Виручка: %.0f грн", total));
    subtitleLabel.setText("Всі замовлення");
    for (Order order : orders) {
      ordersBox.getChildren().add(DashboardCardFactory.orderCard(order));
    }

  }
}
