package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import pizzeria.application.contract.CartService;
import pizzeria.application.contract.OrderService;
import pizzeria.application.impl.OrderServiceImpl;
import pizzeria.domain.entities.OrderItem;
import pizzeria.domain.entities.Pizza;
import pizzeria.domain.enums.OrderType;
import pizzeria.domain.enums.PizzaSize;
import pizzeria.infrastructure.persistence.impl.OrderItemRepositoryImpl;
import pizzeria.infrastructure.persistence.impl.OrderRepositoryImpl;
import pizzeria.infrastructure.session.UserSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartController {

  @FXML private VBox cartItemsBox;
  @FXML private VBox emptyState;
  @FXML private Label totalLabel;
  @FXML private Label itemCountLabel;
  @FXML private Label cartSubtitle;
  @FXML private TextField tableField;
  @FXML private TextField streetField;
  @FXML private TextField houseNumberField;
  @FXML private TextField phoneField;
  @FXML private javafx.scene.control.TextArea commentField;
  @FXML private ChoiceBox<String> orderTypeChoice;
  @FXML private VBox tableSection;
  @FXML private VBox deliverySection;

  private static CartController instance;
  private OrderType orderType = OrderType.DINE_IN;

  public static CartController getInstance() { return instance; }

  @FXML
  public void initialize() {
    instance = this;
    orderTypeChoice.getItems().addAll("В залі", "Із собою", "Доставка");
    orderTypeChoice.setValue("В залі");
    orderTypeChoice.setOnAction(e -> onOrderTypeChanged());
    refresh();
  }

  private void onOrderTypeChanged() {
    String selected = orderTypeChoice.getValue();
    switch (selected) {
      case "В залі" -> {
        orderType = OrderType.DINE_IN;
        tableSection.setVisible(true);
        tableSection.setManaged(true);
        deliverySection.setVisible(false);
        deliverySection.setManaged(false);
      }
      case "Із собою" -> {
        orderType = OrderType.TAKEAWAY;
        tableSection.setVisible(false);
        tableSection.setManaged(false);
        deliverySection.setVisible(false);
        deliverySection.setManaged(false);
      }
      case "Доставка" -> {
        orderType = OrderType.DELIVERY;
        tableSection.setVisible(false);
        tableSection.setManaged(false);
        deliverySection.setVisible(true);
        deliverySection.setManaged(true);
      }
    }
  }

  public void refresh() {
    if (cartItemsBox == null) return;

    CartService cart = MainController.getCartService();
    Map<Pizza, Integer> items = cart.getItems();

    cartItemsBox.getChildren().clear();

    boolean isEmpty = items.isEmpty();
    emptyState.setVisible(isEmpty);
    emptyState.setManaged(isEmpty);
    cartItemsBox.setVisible(!isEmpty);
    cartItemsBox.setManaged(!isEmpty);

    int totalCount = 0;
    for (Map.Entry<Pizza, Integer> entry : items.entrySet()) {
      cartItemsBox.getChildren().add(buildCartItemCard(entry.getKey(), entry.getValue()));
      totalCount += entry.getValue();
    }

    double total = cart.getTotal();
    totalLabel.setText(String.format("%.0f грн", total));
    itemCountLabel.setText(String.valueOf(totalCount));
    cartSubtitle.setText(totalCount == 0 ? "0 позицій" : totalCount + " позицій");

    if (MainController.getInstance() != null)
      MainController.getInstance().updateCartBadge();
  }

  private HBox buildCartItemCard(Pizza pizza, int qty) {
    HBox card = new HBox(16);
    card.getStyleClass().add("cart-item-card");
    card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    StackPane photo = new StackPane();
    photo.getStyleClass().add("cart-item-photo");
    String imgFile = MenuController.getPizzaImageFile(pizza.getName());
    if (imgFile != null) {
      var url = getClass().getResource("/images/" + imgFile);
      if (url != null) {
        Image image = new Image(url.toExternalForm(), 58, 58, false, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(58);
        imageView.setFitHeight(58);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        photo.getChildren().add(imageView);
      }
    }

    VBox info = new VBox(4);
    HBox.setHgrow(info, Priority.ALWAYS);
    Label name = new Label(pizza.getName());
    name.getStyleClass().add("cart-item-name");
    Label details = new Label(String.format("%.0f грн за шт.", pizza.getPrice()));
    details.getStyleClass().add("cart-item-details");
    info.getChildren().addAll(name, details);

    HBox qtyBox = new HBox(8);
    qtyBox.setAlignment(javafx.geometry.Pos.CENTER);
    Button minus = new Button("\u2212");
    minus.getStyleClass().add("qty-btn");
    Label qtyLabel = new Label(String.valueOf(qty));
    qtyLabel.getStyleClass().add("qty-label");
    Button plus = new Button("+");
    plus.getStyleClass().add("qty-btn");

    minus.setOnAction(e -> {
      MainController.getCartService().removePizza(pizza);
      refresh();
    });
    plus.setOnAction(e -> {
      MainController.getCartService().addPizza(pizza);
      refresh();
    });
    qtyBox.getChildren().addAll(minus, qtyLabel, plus);

    Label price = new Label(String.format("%.0f грн", pizza.getPrice() * qty));
    price.getStyleClass().add("cart-item-price");

    Button remove = new Button("\u2715");
    remove.getStyleClass().add("cart-remove-btn");
    remove.setOnAction(e -> {
      for (int i = 0; i < qty; i++) MainController.getCartService().removePizza(pizza);
      refresh();
    });

    card.getChildren().addAll(photo, info, qtyBox, price, remove);
    return card;
  }

  @FXML
  private void handleCheckout() {
    CartService cart = MainController.getCartService();
    if (cart.getItems().isEmpty()) return;

    Integer tableNumber = null;
    String deliveryAddress = null;
    String phone = null;
    String comment = null;
    if (orderType == OrderType.DINE_IN) {
      String tableText = tableField.getText().trim();
      if (tableText.isEmpty()) {
        MainController.showToast("Вкажи номер столика", false);
        return;
      }
      try {
        tableNumber = Integer.parseInt(tableText);
        if (tableNumber <= 0) {
          MainController.showToast("Невірний номер столика", false);
          return;
        }
      } catch (NumberFormatException ignored) {
        MainController.showToast("Номер столика має бути числом", false);
        return;
      }
    } else if (orderType == OrderType.DELIVERY) {
      String street = streetField.getText().trim();
      String houseNumber = houseNumberField.getText().trim();
      deliveryAddress = street + ", " + houseNumber;
      phone = phoneField.getText().trim();
      comment = commentField.getText().trim();
      if (street.isBlank()) {
        MainController.showToast("Вкажи вулицю", false);
        return;
      }
      if (!street.toLowerCase().startsWith("вул.")) {
        MainController.showToast("Вулиця має починатися з \"вул.\"", false);
        return;
      }
      if (houseNumber.isBlank()) {
        MainController.showToast("Вкажи номер будинку", false);
        return;
      }
      if (phone.isBlank()) {
        MainController.showToast("Вкажи телефон", false);
        return;
      }
      if (!phone.matches("^\\+?\\d{10,13}$")) {
        MainController.showToast("Телефон має містити 10-13 цифр", false);
        return;
      }
    }

    List<OrderItem> items = new ArrayList<>();
    for (Map.Entry<Pizza, Integer> entry : cart.getItems().entrySet()) {
      items.add(new OrderItem(
          UUID.randomUUID(),
          null,
          entry.getKey().getId(),
          extractSize(entry.getKey().getName()),
          entry.getValue(),
          entry.getKey().getPrice()
      ));
    }

    OrderService orderService = new OrderServiceImpl(
        new OrderRepositoryImpl(), new OrderItemRepositoryImpl());
    orderService.createOrder(
        UserSession.getCurrentUser().getId(),
        items, tableNumber, orderType, deliveryAddress, phone, comment);

    cart.clear();
    tableField.clear();
    streetField.clear();
    houseNumberField.clear();
    phoneField.clear();
    commentField.clear();
    refresh();
    OrdersController.refreshOrders();
    MainController.showToast("Додано до \"Замовлення\"");
  }

  @FXML
  private void handleClear() {
    MainController.getCartService().clear();
    refresh();
  }

  private PizzaSize extractSize(String pizzaName) {
    if (pizzaName.contains("(S)")) return PizzaSize.S;
    if (pizzaName.contains("(L)")) return PizzaSize.L;
    return PizzaSize.M;
  }
}
