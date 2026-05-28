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
import pizzeria.domain.enums.Role;
import pizzeria.infrastructure.session.UserSession;

public class MainController {

  @FXML private TabPane mainTabPane;
  @FXML private Button btnMenu, btnCart, btnOrders, btnKitchen, btnCourier, btnAdmin, btnUsers, btnUpdateMenu, btnProfile, btnSettings;
  @FXML private Label cartBadge;

  @FXML private AnchorPane menuTab, cartTab, ordersTab, kitchenTab, courierTab, adminTab, usersTab, updateMenuTab, profileTab, settingsTab;

  private static MainController instance;
  private static final CartService cartService = new CartServiceImpl();

  public static CartService getCartService() { return cartService; }
  public static MainController getInstance() { return instance; }

  @FXML
  public void initialize() {
    instance = this;
    loadTab(menuTab, "user/menu-tab");
    loadTab(cartTab, "user/cart-tab");
    loadTab(ordersTab, "user/orders-tab");
    loadTab(kitchenTab, "kitchen/kitchen-tab");
    loadTab(courierTab, "courier/courier-tab");
    loadTab(adminTab, "admin/admin-tab");
    loadTab(usersTab, "admin/users-tab");
    loadTab(updateMenuTab, "admin/update-menu-tab");
    loadTab(profileTab, "profile-tab");
    loadTab(settingsTab, "settings-tab");
    if (cartService instanceof CartServiceImpl cart) {
      cart.reloadForCurrentUser();
    }
    configureRoleAccess();
    updateCartBadge();
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
  @FXML private void showKitchen()  { switchTab(3, btnKitchen); KitchenController.refreshDashboard(); }
  @FXML private void showCourier()  { switchTab(4, btnCourier); CourierController.refreshDashboard(); }
  @FXML private void showAdmin()       { switchTab(5, btnAdmin); AdminController.refreshDashboard(); }
  @FXML private void showUsers()       { switchTab(6, btnUsers); }
  @FXML private void showUpdateMenu()  { switchTab(7, btnUpdateMenu); }
  @FXML private void showProfile()     { switchTab(8, btnProfile); }
  @FXML private void showSettings()    { switchTab(9, btnSettings); }

  private void switchTab(int index, Button active) {
    mainTabPane.getSelectionModel().select(index);
    for (Button btn : new Button[]{btnMenu, btnCart, btnOrders, btnKitchen, btnCourier, btnAdmin, btnUsers, btnUpdateMenu, btnProfile, btnSettings}) {
      btn.getStyleClass().remove("nav-button-active");
    }
    if (!active.getStyleClass().contains("nav-button-active")) {
      active.getStyleClass().add("nav-button-active");
    }
    updateCartBadge();
  }

  private void configureRoleAccess() {
    Role role = UserSession.getCurrentUser() == null ? Role.USER : UserSession.getCurrentUser().getRole();
    for (Button btn : new Button[]{btnMenu, btnCart, btnOrders, btnKitchen, btnCourier, btnAdmin, btnUsers, btnUpdateMenu, btnProfile, btnSettings}) {
      btn.setVisible(false);
      btn.setManaged(false);
    }
    cartBadge.setVisible(true);
    cartBadge.setManaged(true);

    switch (role) {
      case ADMIN -> {
        btnAdmin.setVisible(true); btnAdmin.setManaged(true);
        btnUsers.setVisible(true); btnUsers.setManaged(true);
        btnUpdateMenu.setVisible(true); btnUpdateMenu.setManaged(true);
        btnProfile.setVisible(true); btnProfile.setManaged(true);
        btnSettings.setVisible(true); btnSettings.setManaged(true);
        switchTab(5, btnAdmin);
      }
      case CHEF -> {
        btnKitchen.setVisible(true); btnKitchen.setManaged(true);
        btnProfile.setVisible(true); btnProfile.setManaged(true);
        btnSettings.setVisible(true); btnSettings.setManaged(true);
        switchTab(3, btnKitchen);
      }
      case COURIER -> {
        btnCourier.setVisible(true); btnCourier.setManaged(true);
        btnProfile.setVisible(true); btnProfile.setManaged(true);
        btnSettings.setVisible(true); btnSettings.setManaged(true);
        switchTab(4, btnCourier);
      }
      default -> {
        btnMenu.setVisible(true); btnMenu.setManaged(true);
        btnCart.setVisible(true); btnCart.setManaged(true);
        btnOrders.setVisible(true); btnOrders.setManaged(true);
        btnProfile.setVisible(true); btnProfile.setManaged(true);
        btnSettings.setVisible(true); btnSettings.setManaged(true);
        showMenu();
      }
    }
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
      UserSession.clear();
      if (cartService instanceof CartServiceImpl cart) {
        cart.clearLocalOnly();
      }
      Main.setRoot("login-view", "UrPizza — Авторизація");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
