package pizzeria.presentation.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import pizzeria.Main;
import pizzeria.application.impl.CartServiceImpl;

public class SettingsController {

  @FXML private Button btnDark;
  @FXML private Button btnLight;
  @FXML private Button persistCartButton;

  private static final String DARK_CSS  = "/css/main.css";
  private static final String LIGHT_CSS = "/css/main-light.css";

  @FXML
  public void initialize() {
    Platform.runLater(this::syncThemeButtons);
    Platform.runLater(this::syncPersistButton);
  }

  private void syncThemeButtons() {
    Scene scene = btnDark.getScene();
    if (scene != null) {
      boolean isLight = Main.isLightTheme();
      if (isLight) {
        markActive(btnLight, btnDark);
      } else {
        markActive(btnDark, btnLight);
      }
    } else {
      markActive(btnDark, btnLight);
    }
  }

  @FXML
  private void setDarkTheme() {
    applyTheme(false);
    Main.setLightTheme(false);
    markActive(btnDark, btnLight);
  }

  @FXML
  private void setLightTheme() {
    applyTheme(true);
    Main.setLightTheme(true);
    markActive(btnLight, btnDark);
  }

  private void applyTheme(boolean light) {
    try {
      Scene scene = btnDark.getScene();
      if (scene == null) return;

      String mainUrl = getClass().getResource(DARK_CSS).toExternalForm();
      String lightUrl = getClass().getResource(LIGHT_CSS).toExternalForm();

      scene.getStylesheets().clear();
      scene.getStylesheets().add(mainUrl);

      if (light) {
        scene.getStylesheets().add(lightUrl);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handlePersistCartToggle() {
    boolean enabled = !CartServiceImpl.isPersistEnabled();
    CartServiceImpl.setPersistEnabled(enabled);
    if (enabled && MainController.getCartService() instanceof CartServiceImpl cartService) {
      cartService.persistCurrentCart();
    }
    syncPersistButton();
    MainController.showToast(enabled
        ? "Збереження кошика увімкнено"
        : "Збереження кошика вимкнено");
  }

  private void syncPersistButton() {
    if (persistCartButton == null) return;
    boolean enabled = CartServiceImpl.isPersistEnabled();
    persistCartButton.setText(enabled ? "Увімкнено" : "Вимкнено");
    persistCartButton.getStyleClass().remove("persist-btn-active");
    if (enabled) {
      persistCartButton.getStyleClass().add("persist-btn-active");
    }
  }

  private void markActive(Button active, Button inactive) {
    if (!active.getStyleClass().contains("theme-btn-active")) {
      active.getStyleClass().add("theme-btn-active");
    }

    inactive.getStyleClass().remove("theme-btn-active");
    if (!inactive.getStyleClass().contains("theme-btn")) {
      inactive.getStyleClass().add("theme-btn");
    }
  }

  @FXML
  private void handleClearCart() {
    MainController.getCartService().clear();
    if (CartController.getInstance() != null) {
      CartController.getInstance().refresh();
    }
    if (MainController.getInstance() != null) {
      MainController.getInstance().updateCartBadge();
    }

    MainController.showToast("Кошик очищено");
  }
}
