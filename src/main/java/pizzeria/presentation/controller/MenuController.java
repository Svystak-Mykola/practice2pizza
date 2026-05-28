package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import pizzeria.application.contract.PizzaService;
import pizzeria.application.impl.PizzaServiceImpl;
import pizzeria.domain.entities.Pizza;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MenuController {

  @FXML private TextField searchField;
  @FXML private FlowPane menuGrid;
  @FXML private HBox categoryBar;
  @FXML private Label menuSubtitle;

  private final PizzaService pizzaService = new PizzaServiceImpl();
  private List<Pizza> allPizzas = new ArrayList<>();
  private String activeCategory = "Всі";

  public static final Map<String, String> PIZZA_IMAGES = Map.ofEntries(
      Map.entry("Маргарита",      "margarita.jpg"),
      Map.entry("Чотири сири",    "4sira.jpg"),
      Map.entry("Пепероні",       "peperoni.jpg"),
      Map.entry("М'ясна",         "mnyasna.jpg"),
      Map.entry("Мексиканська",   "mexicanska.jpg"),
      Map.entry("Чоріззо",        "chorizzo.jpg"),
      Map.entry("Поло",           "polo.jpg"),
      Map.entry("Цезарь",         "cezar.jpg"),
      Map.entry("Аматрічіано",    "amatraciano.jpg"),
      Map.entry("Мисливська",     "mislivska.jpg"),
      Map.entry("Гавайська",      "havaiska.jpg"),
      Map.entry("Шинка та гриби", "shinaka-gribi.jpg"),
      Map.entry("Салямі",         "salami.jpg")
  );

  public static String getPizzaImageFile(String pizzaName) {
    String cleanName = pizzaName.replaceAll("\\s*\\([SML]\\)$", "");
    return PIZZA_IMAGES.get(cleanName);
  }

  @FXML
  public void initialize() {
    allPizzas = pizzaService.getAllPizzas();
    buildCategoryBar();
    renderPizzas(allPizzas);
    menuSubtitle.setText(allPizzas.size() + " позицій");
  }

  private void buildCategoryBar() {
    categoryBar.getChildren().clear();
    Set<String> categories = new LinkedHashSet<>();
    categories.add("Всі");
    for (Pizza p : allPizzas) {
      if (p.getCategoryName() != null && !p.getCategoryName().isBlank()) {
        categories.add(p.getCategoryName());
      }
    }
    for (String cat : categories) {
      Button chip = new Button(cat);
      chip.getStyleClass().add("category-chip");
      if (cat.equals(activeCategory)) chip.getStyleClass().add("category-chip-active");
      chip.setOnAction(e -> {
        activeCategory = cat;
        buildCategoryBar();
        applyFilter();
      });
      categoryBar.getChildren().add(chip);
    }
  }

  @FXML
  private void onSearch(KeyEvent event) {
    applyFilter();
  }

  private void applyFilter() {
    String query = searchField.getText().toLowerCase().trim();
    List<Pizza> filtered = allPizzas.stream()
        .filter(p -> query.isEmpty() || p.getName().toLowerCase().contains(query))
        .filter(p -> activeCategory.equals("Всі") ||
            (p.getCategoryName() != null && p.getCategoryName().equals(activeCategory)))
        .toList();
    renderPizzas(filtered);
  }

  private void renderPizzas(List<Pizza> pizzas) {
    menuGrid.getChildren().clear();
    for (Pizza pizza : pizzas) {
      menuGrid.getChildren().add(buildPizzaCard(pizza));
    }
  }

  private Node buildPizzaCard(Pizza pizza) {
    VBox card = new VBox();
    card.getStyleClass().add("pizza-card");
    card.setSpacing(0);

    StackPane imgArea = new StackPane();
    imgArea.getStyleClass().add("pizza-img-placeholder");

    String imgFile = getPizzaImageFile(pizza.getName());
    Image customImg = AdminController.loadCustomImage(pizza.getName(), 220, 140);
    if (customImg != null) {
      ImageView iv = new ImageView(customImg);
      iv.setFitWidth(220);
      iv.setFitHeight(140);
      iv.setPreserveRatio(false);
      iv.setSmooth(true);
      imgArea.getChildren().add(iv);
    } else if (imgFile != null) {
      try {
        var url = getClass().getResource("/images/" + imgFile);
        if (url != null) {
          Image image = new Image(url.toExternalForm(), 220, 140, false, true);
          ImageView iv = new ImageView(image);
          iv.setFitWidth(220);
          iv.setFitHeight(140);
          iv.setPreserveRatio(false);
          iv.setSmooth(true);
          imgArea.getChildren().add(iv);
        }
      } catch (Exception ignored) {}
    }

    VBox body = new VBox();
    body.getStyleClass().add("pizza-card-body");
    body.setSpacing(4);
    VBox.setVgrow(body, Priority.ALWAYS);

    Label nameLabel = new Label(pizza.getName());
    nameLabel.getStyleClass().add("pizza-card-name");
    nameLabel.setWrapText(true);

    Label priceLabel = new Label(String.format("%.0f грн", pizza.getPrice()));
    priceLabel.getStyleClass().add("pizza-card-price");
    priceLabel.setStyle("-fx-padding: 6 0 0 0;");

    Label ingredientsLabel = new Label("Інгредієнти: " +
        (pizza.getIngredients() == null ? "" : pizza.getIngredients()));
    ingredientsLabel.getStyleClass().add("pizza-card-ingredients");
    ingredientsLabel.setWrapText(true);
    ingredientsLabel.setVisible(false);
    ingredientsLabel.setManaged(false);

    Button moreBtn = new Button("Більше");
    moreBtn.getStyleClass().add("pizza-more-btn");
    moreBtn.setOnAction(e -> {
      boolean expanded = !ingredientsLabel.isVisible();
      ingredientsLabel.setVisible(expanded);
      ingredientsLabel.setManaged(expanded);
      card.getStyleClass().remove("pizza-card-expanded");
      if (expanded) {
        card.getStyleClass().add("pizza-card-expanded");
      }
      e.consume();
    });

    body.getChildren().addAll(nameLabel, priceLabel, moreBtn, ingredientsLabel);

    Button addBtn = new Button("Обрати розмір →");
    addBtn.getStyleClass().add("add-to-cart-btn");
    addBtn.setMaxWidth(Double.MAX_VALUE);
    addBtn.setOnAction(e -> openSizePicker(pizza));

    card.getChildren().addAll(imgArea, body, addBtn);
    return card;
  }

  private void openSizePicker(Pizza pizza) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/fxml/user/size-picker-dialog.fxml"));
      Node modal = loader.load();

      SizePickerController ctrl = loader.getController();
      ctrl.setPizza(pizza);
      ctrl.setOnConfirm(() -> {
        removeModalFromScene(modal);
        MainController.getInstance().updateCartBadge();
        MainController.showToast("Додано до кошику");
      });
      ctrl.setOnCancel(() -> removeModalFromScene(modal));

      StackPane overlay = getOrCreateOverlay();
      overlay.getChildren().add(modal);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private StackPane getOrCreateOverlay() {
    javafx.scene.Scene scene = menuGrid.getScene();
    if (scene.getRoot() instanceof StackPane sp) {
      return sp;
    }
    StackPane sp = new StackPane(scene.getRoot());
    scene.setRoot(sp);
    return sp;
  }

  private void removeModalFromScene(Node modal) {
    javafx.scene.Scene scene = menuGrid.getScene();
    if (scene != null && scene.getRoot() instanceof StackPane sp) {
      sp.getChildren().remove(modal);
    }
  }
}
