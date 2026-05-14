package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
  @FXML private Button btnDineIn, btnTakeaway;
  @FXML private VBox tableSection;

  private static CartController instance;
  private String orderType = "DINE_IN";

  public static CartController getInstance() { return instance; }

  @FXML
  public void initialize() {
    instance = this;
    refresh();
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
    Button minus = new Button("−");
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

    Button remove = new Button("✕");
    remove.getStyleClass().add("cart-remove-btn");
    remove.setOnAction(e -> {
      for (int i = 0; i < qty; i++) MainController.getCartService().removePizza(pizza);
      refresh();
    });

    card.getChildren().addAll(photo, info, qtyBox, price, remove);
    return card;
  }

  @FXML private void setDineIn() {
    orderType = "DINE_IN";
    tableSection.setVisible(true);
    tableSection.setManaged(true);
    btnDineIn.getStyleClass().add("order-type-btn-active");
    btnTakeaway.getStyleClass().remove("order-type-btn-active");
  }

  @FXML private void setTakeaway() {
    orderType = "TAKEAWAY";
    tableSection.setVisible(false);
    tableSection.setManaged(false);
    btnTakeaway.getStyleClass().add("order-type-btn-active");
    btnDineIn.getStyleClass().remove("order-type-btn-active");
  }

  @FXML
  private void handleCheckout() {
    CartService cart = MainController.getCartService();
    if (cart.getItems().isEmpty()) return;

    Integer tableNumber = null;
    if (orderType.equals("DINE_IN")) {
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
        items, tableNumber, orderType);

    cart.clear();
    tableField.clear();
    refresh();
    OrdersController.refreshOrders();
    MainController.showToast("Додано до \"Замовлення\"");
  }

  @FXML
  private void handleClear() {
    MainController.getCartService().clear();
    refresh();
  }

  private String extractSize(String pizzaName) {
    if (pizzaName.contains("(S)")) return "S";
    if (pizzaName.contains("(L)")) return "L";
    return "M";
  }
}
