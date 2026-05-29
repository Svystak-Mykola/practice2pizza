package pizzeria.presentation.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pizzeria.domain.entities.Order;
import pizzeria.domain.entities.OrderItem;
import pizzeria.domain.enums.OrderType;
import pizzeria.infrastructure.persistence.impl.OrderItemRepositoryImpl;

import java.time.format.DateTimeFormatter;
import java.util.List;

final class DashboardCardFactory {
  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM HH:mm");
  private static final OrderItemRepositoryImpl ITEM_REPO = new OrderItemRepositoryImpl();

  private DashboardCardFactory() {}

  static VBox orderCard(Order order, Button... actions) {
    VBox card = new VBox(10);
    card.getStyleClass().add("order-card");

    HBox top = new HBox(12);
    Label id = new Label("#" + order.getId().toString().substring(0, 8).toUpperCase());
    id.getStyleClass().add("order-id");
    Label time = new Label(order.getCreatedAt().format(FMT));
    time.getStyleClass().add("order-date");
    Label status = new Label(order.getStatus().displayName());
    status.getStyleClass().add("order-status-accepted");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    top.getChildren().addAll(id, time, spacer, status);

    Label meta = new Label(metaText(order));
    meta.getStyleClass().add("order-date");
    meta.setWrapText(true);

    Label items = new Label(itemsText(order));
    items.getStyleClass().add("receipt-text");
    items.setWrapText(true);

    HBox buttons = new HBox(8);
    buttons.getChildren().addAll(actions);

    card.getChildren().addAll(top, meta, items, buttons);
    return card;
  }

  private static String metaText(Order order) {
    if (OrderType.DELIVERY == order.getOrderType()) {
      return "Доставка: " + nullSafe(order.getDeliveryAddress()) + " " + nullSafe(order.getPhone());
    }
    if (OrderType.TAKEAWAY == order.getOrderType()) {
      return "Із собою";
    }
    return order.getTableNumber() == null ? "В залі" : "В залі стіл " + order.getTableNumber();
  }

  private static String itemsText(Order order) {
    List<OrderItem> items = ITEM_REPO.findByOrderId(order.getId());
    if (items.isEmpty()) return "Склад замовлення порожній";
    StringBuilder out = new StringBuilder("Склад: ");
    for (OrderItem item : items) {
      String name = item.getPizzaName() != null ? item.getPizzaName() : "?";
      String ingredients = item.getPizzaIngredients() != null ? " — " + item.getPizzaIngredients() : "";
      out.append(name).append(" (").append(item.getSizeName()).append(" x").append(item.getQuantity()).append(")").append(ingredients).append("  ");
    }
    return out.toString();
  }

  private static String nullSafe(String value) {
    return value == null || value.isBlank() ? "-" : value;
  }
}
