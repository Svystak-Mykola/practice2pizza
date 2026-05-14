package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import pizzeria.domain.entities.Order;
import pizzeria.domain.entities.OrderItem;
import pizzeria.infrastructure.persistence.contract.OrderItemRepository;
import pizzeria.infrastructure.persistence.contract.OrderRepository;
import pizzeria.infrastructure.persistence.impl.OrderItemRepositoryImpl;
import pizzeria.infrastructure.persistence.impl.OrderRepositoryImpl;
import pizzeria.infrastructure.session.UserSession;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrdersController {

  @FXML private VBox ordersBox;
  @FXML private VBox ordersEmpty;
  @FXML private VBox receiptBox;
  @FXML private Label ordersSubtitle;

  private final OrderRepository orderRepo = new OrderRepositoryImpl();
  private final OrderItemRepository itemRepo = new OrderItemRepositoryImpl();
  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm");
  private static OrdersController instance;

  @FXML
  public void initialize() {
    instance = this;
    loadOrders();
  }

  public static void refreshOrders() {
    if (instance != null) {
      instance.loadOrders();
    }
  }

  private void loadOrders() {
    UUID userId = UserSession.getCurrentUser().getId();
    List<Order> orders = orderRepo.findAll().stream()
        .filter(o -> o.getUserId().equals(userId))
        .collect(Collectors.toList());

    ordersBox.getChildren().clear();

    boolean empty = orders.isEmpty();
    ordersEmpty.setVisible(empty);
    ordersEmpty.setManaged(empty);

    ordersSubtitle.setText(orders.size() == 0 ? "Немає замовлень" : orders.size() + " замовлень");

    for (Order order : orders) {
      ordersBox.getChildren().add(buildOrderCard(order));
    }
  }

  private HBox buildOrderCard(Order order) {
    HBox card = new HBox(16);
    card.getStyleClass().add("order-card");
    card.setAlignment(Pos.CENTER_LEFT);

    VBox info = new VBox(5);
    HBox.setHgrow(info, Priority.ALWAYS);

    Label idLabel = new Label("#" + order.getId().toString().substring(0, 8).toUpperCase());
    idLabel.getStyleClass().add("order-id");

    Label dateLabel = new Label(order.getCreatedAt().format(FMT));
    dateLabel.getStyleClass().add("order-date");

    String typeText = "DINE_IN".equals(order.getOrderType()) ? "🍽 В залі" : "🥡 Із собою";
    if (order.getTableNumber() != null)
      typeText += " · Стіл " + order.getTableNumber();
    Label typeLabel = new Label(typeText);
    typeLabel.getStyleClass().add("order-date");

    info.getChildren().addAll(idLabel, dateLabel, typeLabel);

    VBox right = new VBox(6);
    right.setAlignment(Pos.CENTER_RIGHT);

    Label amount = new Label(String.format("%.0f грн", order.getTotalAmount()));
    amount.getStyleClass().add("order-amount");

    Label status = new Label(order.getStatus());
    status.getStyleClass().add("order-status-accepted");

    right.getChildren().addAll(amount, status);

    card.getChildren().addAll(info, right);
    card.setOnMouseClicked(e -> showReceipt(order));

    return card;
  }

  private void showReceipt(Order order) {
    receiptBox.getChildren().clear();

    List<OrderItem> items = itemRepo.findByOrderId(order.getId());

    VBox content = new VBox(12);

    Label title = new Label("UrPizza");
    title.getStyleClass().add("receipt-title");

    Label date = new Label(order.getCreatedAt().format(FMT));
    date.getStyleClass().add("receipt-text");

    Label orderId = new Label("Замовлення: #" + order.getId().toString().substring(0, 8).toUpperCase());
    orderId.getStyleClass().add("receipt-text");

    String typeStr = "DINE_IN".equals(order.getOrderType()) ? "В залі" : "Із собою";
    Label type = new Label("Тип: " + typeStr);
    type.getStyleClass().add("receipt-text");

    if (order.getTableNumber() != null) {
      Label table = new Label("Столик: " + order.getTableNumber());
      table.getStyleClass().add("receipt-text");
      content.getChildren().add(table);
    }

    Pane line1 = new Pane();
    line1.getStyleClass().add("receipt-line");
    line1.setMaxWidth(Double.MAX_VALUE);

    content.getChildren().addAll(title, date, orderId, type, line1);

    for (OrderItem item : items) {
      HBox row = new HBox();
      Label itemName = new Label(item.getSize() + "  ×" + item.getQuantity());
      itemName.getStyleClass().add("receipt-text");
      itemName.setStyle("-fx-text-fill: #666;");

      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);

      Label itemPrice = new Label(String.format("%.0f грн", item.getPriceAtTime() * item.getQuantity()));
      itemPrice.getStyleClass().add("receipt-text");

      row.getChildren().addAll(itemName, spacer, itemPrice);
      content.getChildren().add(row);
    }

    Pane line2 = new Pane();
    line2.getStyleClass().add("receipt-line");
    line2.setMaxWidth(Double.MAX_VALUE);

    HBox totalRow = new HBox();
    Label totalText = new Label("РАЗОМ");
    totalText.getStyleClass().add("receipt-total-label");
    Region sp2 = new Region();
    HBox.setHgrow(sp2, Priority.ALWAYS);
    Label totalAmt = new Label(String.format("%.0f грн", order.getTotalAmount()));
    totalAmt.setStyle("-fx-text-fill: #ff7a00; -fx-font-family: 'Bebas Neue'; -fx-font-size: 22px;");
    totalRow.getChildren().addAll(totalText, sp2, totalAmt);

    Label statusLine = new Label("Статус: " + order.getStatus());
    statusLine.getStyleClass().add("order-status-accepted");

    content.getChildren().addAll(line2, totalRow, statusLine);

    VBox.setVgrow(content, Priority.ALWAYS);
    receiptBox.getChildren().add(content);
  }
}
