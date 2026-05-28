package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pizzeria.domain.entities.User;
import pizzeria.infrastructure.persistence.impl.UserRepositoryImpl;

import java.util.List;

public class UsersController {

  @FXML private VBox usersBox;
  @FXML private Label subtitleLabel;

  private final UserRepositoryImpl userRepo = new UserRepositoryImpl();

  @FXML
  public void initialize() {
    loadUsers();
  }

  private void loadUsers() {
    usersBox.getChildren().clear();
    List<User> users = userRepo.findAll();
    subtitleLabel.setText("Користувачів: " + users.size());
    for (User user : users) {
      VBox panel = new VBox();
      panel.getStyleClass().add("settings-card");
      panel.setSpacing(0);
      panel.getChildren().add(buildUserCard(user));
      usersBox.getChildren().add(panel);
    }
  }

  private HBox buildUserCard(User user) {
    HBox card = new HBox(12);
    card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    VBox info = new VBox(4);
    Label nameLabel = new Label(user.getName());
    nameLabel.getStyleClass().add("settings-label");
    Label emailLabel = new Label(user.getEmail());
    emailLabel.getStyleClass().add("order-date");
    info.getChildren().addAll(nameLabel, emailLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Label currentRole = new Label("Роль: " + user.getRole().displayName());
    currentRole.getStyleClass().add("order-date");

    ChoiceBox<String> roleChoice = new ChoiceBox<>();
    roleChoice.getItems().addAll("USER", "ADMIN", "CHEF", "COURIER");
    roleChoice.setValue(user.getRoleName());
    roleChoice.getStyleClass().add("table-field");
    roleChoice.setOnAction(e -> {
      String newRole = roleChoice.getValue();
      if (newRole != null && !newRole.equals(user.getRoleName())) {
        user.setRoleName(newRole);
        userRepo.save(user);
        currentRole.setText("Роль: " + user.getRole().displayName());
        MainController.showToast("Роль \"" + user.getName() + "\" змінена на " + user.getRole().displayName());
      }
    });

    card.getChildren().addAll(info, spacer, currentRole, roleChoice);
    return card;
  }
}
