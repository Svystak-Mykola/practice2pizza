package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pizzeria.Main;
import pizzeria.application.impl.AuthServiceImpl;
import java.util.regex.Pattern;

public class RegisterController {
  @FXML private TextField nameField, emailField, phoneField;
  @FXML private PasswordField passwordField, confirmPasswordField;
  @FXML private Label statusLabel;

  private final AuthServiceImpl authService = new AuthServiceImpl();
  private static final Pattern EMAIL_REGEX =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  @FXML
  private void handleRegister() {
    String name = nameField.getText().trim();
    String email = emailField.getText().trim();
    String phone = phoneField.getText().trim();
    String pass = passwordField.getText();
    String confirmPass = confirmPasswordField.getText();

    if (name.isEmpty()) {
      showStatus("Введи ім'я.", false);
      return;
    }
    if (!EMAIL_REGEX.matcher(email).matches()) {
      showStatus("Введи коректний email.", false);
      return;
    }
    if (!phone.isEmpty() && !phone.matches("^\\+?\\d{10,13}$")) {
      showStatus("Телефон має містити 10-13 цифр.", false);
      return;
    }
    if (pass.length() < 6) {
      showStatus("Пароль має бути мінімум 6 символів.", false);
      return;
    }
    if (!pass.equals(confirmPass)) {
      showStatus("Паролі не співпадають.", false);
      return;
    }

    try {
      authService.register(name, email, pass, phone.isEmpty() ? null : phone);
      showStatus("Акаунт створено. Можна входити.", true);
    } catch (Exception e) {
      showStatus(e.getMessage(), false);
    }
  }

  private void showStatus(String message, boolean success) {
    statusLabel.setText(message);
    statusLabel.setStyle("-fx-text-fill: " + (success ? "#2e7d32" : "#ff3b30") + "; -fx-font-size: 13px;");
  }

  @FXML private void backToLogin() throws Exception { Main.setRoot("login-view", "UrPizza — Авторизація"); }
}
