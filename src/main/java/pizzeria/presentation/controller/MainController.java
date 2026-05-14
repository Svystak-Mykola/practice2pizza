package pizzeria.presentation.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import pizzeria.Main;
import pizzeria.application.contract.CartService;
import pizzeria.application.impl.CartServiceImpl;
import pizzeria.infrastructure.session.SessionStorage;
import pizzeria.infrastructure.session.UserSession;

public class MainController {

  @FXML private TabPane mainTabPane;
  @FXML private Button btnMenu, btnCart, btnOrders, btnProfile, btnSettings;
  @FXML private Label cartBadge;

  @FXML private AnchorPane menuTab, cartTab, ordersTab, profileTab, settingsTab;

  private static MainController instance;
  private static final CartService cartService = new CartServiceImpl();

  public static CartService getCartService() { return cartService; }
  public static MainController getInstance() { return instance; }

  @FXML
  public void initialize() {
    instance = this;
    loadTab(menuTab, "menu-tab");
    loadTab(cartTab, "cart-tab");
    loadTab(ordersTab, "orders-tab");
    loadTab(profileTab, "profile-tab");
    loadTab(settingsTab, "settings-tab");
  }

  private void loadTab(AnchorPane pane, String fxmlName) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/fxml/" + fxmlName + ".fxml"));
      Node content = loader.load();
      AnchorPane.setTopAnchor(content, 0.0);
      AnchorPane.setBottomAnchor(content, 0.0);
      AnchorPane.setLeftAnchor(content, 0.0);
      AnchorPane.setRightAnchor(content, 0.0);
      pane.getChildren().add(content);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML private void showMenu()     { switchTab(0, btnMenu); }
  @FXML private void showCart()     { switchTab(1, btnCart); }
  @FXML private void showOrders()   { switchTab(2, btnOrders); OrdersController.refreshOrders(); }
  @FXML private void showProfile()  { switchTab(3, btnProfile); }
  @FXML private void showSettings() { switchTab(4, btnSettings); }

  private void switchTab(int index, Button active) {
    mainTabPane.getSelectionModel().select(index);
    for (Button btn : new Button[]{btnMenu, btnCart, btnOrders, btnProfile, btnSettings}) {
      btn.getStyleClass().remove("nav-button-active");
    }
    if (!active.getStyleClass().contains("nav-button-active")) {
      active.getStyleClass().add("nav-button-active");
    }
    updateCartBadge();
  }

  public void updateCartBadge() {
    int count = cartService.getItems().values().stream().mapToInt(i -> i).sum();
    cartBadge.setText(count == 0 ? "Кошик порожній" : count + " товарів у кошику");
  }

  public static void showToast(String message) {
    showToast(message, true);
  }

  public static void showToast(String message, boolean success) {
    if (instance == null || instance.mainTabPane == null) return;
    Scene scene = instance.mainTabPane.getScene();
    if (scene == null || !(scene.getRoot() instanceof StackPane overlay)) return;

    HBox toast = new HBox(8);
    toast.getStyleClass().add("toast");
    if (!success) toast.getStyleClass().add("toast-error");
    toast.setAlignment(Pos.CENTER);
    toast.setMinWidth(Region.USE_PREF_SIZE);
    toast.setPrefWidth(Region.USE_COMPUTED_SIZE);
    toast.setMaxWidth(Region.USE_PREF_SIZE);
    toast.setMinHeight(Region.USE_PREF_SIZE);
    toast.setPrefHeight(Region.USE_COMPUTED_SIZE);
    toast.setMaxHeight(Region.USE_PREF_SIZE);

    Label icon = new Label(success ? "✓" : "!");
    icon.getStyleClass().add("toast-icon");
    Label text = new Label(message);
    text.getStyleClass().add("toast-text");
    toast.getChildren().addAll(icon, text);

    StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
    StackPane.setMargin(toast, new Insets(0, 0, 22, 0));

    overlay.getChildren().add(toast);

    PauseTransition pause = new PauseTransition(Duration.seconds(2));
    pause.setOnFinished(ev -> overlay.getChildren().remove(toast));
    pause.play();
  }

  @FXML
  private void handleLogout() {
    try {
      SessionStorage.clear();
      UserSession.clear();
      if (!CartServiceImpl.isPersistEnabled()) {
        cartService.clear();
      }
      Main.setRoot("login-view", "UrPizza — Авторизація");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
