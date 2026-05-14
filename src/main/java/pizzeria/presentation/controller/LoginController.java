package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pizzeria.Main;
import pizzeria.application.contract.AuthService;
import pizzeria.application.impl.AuthServiceImpl;
import pizzeria.domain.entities.User;
import pizzeria.infrastructure.session.SessionStorage;
import pizzeria.infrastructure.session.UserSession;

import java.util.regex.Pattern;

public class LoginController {
  @FXML private TextField emailField;
  @FXML private PasswordField passwordField;
  @FXML private Label statusLabel;

  private final AuthService authService = new AuthServiceImpl();
  private static final Pattern EMAIL_REGEX =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  @FXML
  private void handleLogin() {
    String email = emailField.getText() == null ? "" : emailField.getText().trim();
    String password = passwordField.getText() == null ? "" : passwordField.getText();

    if (email.isBlank()) {
      showError("Введи email.");
      return;
    }
    if (!EMAIL_REGEX.matcher(email).matches()) {
      showError("Введи коректний email.");
      return;
    }
    if (password.isBlank()) {
      showError("Введи пароль.");
      return;
    }

    try {
      User user = authService.login(email, password);
      UserSession.setCurrentUser(user);

      SessionStorage.save(user.getEmail());

      Main.setRoot("main-view", "UrPizza — Dashboard");
    } catch (Exception e) {
      showError(e.getMessage());
    }
  }

  private void showError(String message) {
    statusLabel.setText(message);
    statusLabel.setStyle("-fx-text-fill: #ff3b30; -fx-font-size: 13px;");
  }

  @FXML
  private void openRegister() {
    try {
      Main.setRoot("register-view", "UrPizza — Реєстрація");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
