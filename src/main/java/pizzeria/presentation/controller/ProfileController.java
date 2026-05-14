package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.mindrot.jbcrypt.BCrypt;
import pizzeria.Main;
import pizzeria.domain.entities.User;
import pizzeria.infrastructure.persistence.contract.UserRepository;
import pizzeria.infrastructure.persistence.impl.UserRepositoryImpl;
import pizzeria.infrastructure.session.SessionStorage;
import pizzeria.infrastructure.session.UserSession;

import java.io.File;

public class ProfileController {

  @FXML private ImageView avatarImage;
  @FXML private Label profileNameLabel;
  @FXML private Label profileEmailLabel;
  @FXML private TextField nameField;
  @FXML private TextField emailField;
  @FXML private PasswordField currentPassField;
  @FXML private PasswordField newPassField;
  @FXML private PasswordField confirmPassField;
  @FXML private Label infoStatusLabel;
  @FXML private Label passStatusLabel;

  private final UserRepository userRepository = new UserRepositoryImpl();

  @FXML
  public void initialize() {
    User user = UserSession.getCurrentUser();
    if (user == null) return;

    profileNameLabel.setText(user.getName());
    profileEmailLabel.setText(user.getEmail());
    nameField.setText(user.getName());
    emailField.setText(user.getEmail());
    setupAvatarView();
    loadAvatar(user.getAvatarPath());
  }

  private void setupAvatarView() {
    avatarImage.setFitWidth(100);
    avatarImage.setFitHeight(100);
    avatarImage.setPreserveRatio(false);
    avatarImage.setSmooth(true);
    avatarImage.setClip(new Circle(50, 50, 50));
  }

  @FXML
  private void handleAvatarClick() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Обрати аватарку");
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Зображення", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    File file = chooser.showOpenDialog(avatarImage.getScene().getWindow());
    if (file != null) {
      String avatarPath = file.getAbsolutePath();
      loadAvatar(avatarPath);

      User user = UserSession.getCurrentUser();
      if (user != null) {
        user.setAvatarPath(avatarPath);
        userRepository.save(user);
      }
    }
  }

  private void loadAvatar(String avatarPath) {
    if (avatarPath == null || avatarPath.isBlank()) return;
    File file = new File(avatarPath);
    if (!file.exists()) return;

    Image img = new Image(file.toURI().toString());
    double side = Math.min(img.getWidth(), img.getHeight());
    double x = (img.getWidth() - side) / 2;
    double y = (img.getHeight() - side) / 2;
    avatarImage.setViewport(new Rectangle2D(x, y, side, side));
    avatarImage.setImage(img);
    avatarImage.setVisible(true);
  }

  @FXML
  private void handleSaveInfo() {
    String name = nameField.getText().trim();
    String email = emailField.getText().trim();

    if (name.isEmpty() || email.isEmpty()) {
      showStatus(infoStatusLabel, "Заповни всі поля", false);
      return;
    }

    User user = UserSession.getCurrentUser();
    user.setName(name);
    user.setEmail(email);
    userRepository.save(user);

    profileNameLabel.setText(name);
    profileEmailLabel.setText(email);
    showStatus(infoStatusLabel, "✅  Дані збережено", true);
  }

  @FXML
  private void handleChangePassword() {
    User user = UserSession.getCurrentUser();
    String currentPass = currentPassField.getText();
    String newPass = newPassField.getText();
    String confirmPass = confirmPassField.getText();

    if (!passwordMatches(currentPass, user.getPassword())) {
      showStatus(passStatusLabel, "❌  Поточний пароль невірний", false);
      return;
    }
    if (newPass.length() < 6) {
      showStatus(passStatusLabel, "❌  Новий пароль мінімум 6 символів", false);
      return;
    }
    if (!newPass.equals(confirmPass)) {
      showStatus(passStatusLabel, "❌  Паролі не співпадають", false);
      return;
    }

    user.setPassword(BCrypt.hashpw(newPass, BCrypt.gensalt()));
    userRepository.save(user);

    currentPassField.clear();
    newPassField.clear();
    confirmPassField.clear();
    showStatus(passStatusLabel, "✅  Пароль змінено", true);
  }

  @FXML
  private void handleLogout() {
    try {
      SessionStorage.clear();
      UserSession.clear();
      if (!pizzeria.application.impl.CartServiceImpl.isPersistEnabled()) {
        MainController.getCartService().clear();
      }
      Main.setRoot("login-view", "UrPizza — Авторизація");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void showStatus(Label label, String message, boolean success) {
    label.setText(message);
    label.setStyle("-fx-font-size: 13px; -fx-text-fill: "
        + (success ? "#4CAF50" : "#ff3b30") + ";");
  }

  private boolean passwordMatches(String rawPassword, String storedPassword) {
    if (storedPassword == null) return false;
    try {
      if (storedPassword.startsWith("$2")) {
        return BCrypt.checkpw(rawPassword, storedPassword);
      }
    } catch (IllegalArgumentException ignored) {
      return false;
    }
    return storedPassword.equals(rawPassword);
  }
}
