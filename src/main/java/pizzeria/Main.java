package pizzeria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pizzeria.util.DatabaseInitializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimeZone;

public class Main extends Application {
  private static Stage primaryStage;
  private static final Path THEME_FILE = Path.of("pizzeria-theme.settings");
  private static boolean lightTheme = loadLightTheme();

  @Override
  public void start(Stage stage) throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
    primaryStage = stage;
    DatabaseInitializer.initialize();
    setRoot("login-view", "UrPizza — Авторизація");
  }

  public static void setRoot(String fxml, String title) throws Exception {
    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
    Parent root = loader.load();
    javafx.scene.layout.StackPane wrapper = new javafx.scene.layout.StackPane(root);

    Scene scene = new Scene(wrapper);
    if (fxml.contains("main")) {
      scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
      if (lightTheme) {
        scene.getStylesheets().add(Main.class.getResource("/css/main-light.css").toExternalForm());
      }
    } else {
      scene.getStylesheets().add(Main.class.getResource("/css/auth.css").toExternalForm());
      if (lightTheme) {
        scene.getStylesheets().add(Main.class.getResource("/css/auth-light.css").toExternalForm());
      }
    }

    primaryStage.setTitle(title);
    primaryStage.setScene(scene);
    primaryStage.setMinWidth(1100);
    primaryStage.setMinHeight(700);
    var bounds = Screen.getPrimary().getVisualBounds();
    primaryStage.setMaximized(false);
    primaryStage.setX(bounds.getMinX());
    primaryStage.setY(bounds.getMinY());
    primaryStage.setWidth(bounds.getWidth());
    primaryStage.setHeight(bounds.getHeight());
    primaryStage.setMaximized(true);
    primaryStage.show();
    javafx.application.Platform.runLater(() -> primaryStage.setMaximized(true));
  }

  public static Stage getPrimaryStage() { return primaryStage; }
  public static boolean isLightTheme() { return lightTheme; }

  public static void setLightTheme(boolean value) {
    lightTheme = value;
    try {
      Files.writeString(THEME_FILE, value ? "light" : "dark", StandardCharsets.UTF_8);
    } catch (IOException ignored) {
    }
  }

  private static boolean loadLightTheme() {
    try {
      if (Files.exists(THEME_FILE)) {
        return !"dark".equalsIgnoreCase(Files.readString(THEME_FILE, StandardCharsets.UTF_8).trim());
      }
    } catch (IOException ignored) {
    }
    return true;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
