package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pizzeria.application.contract.PizzaService;
import pizzeria.application.impl.PizzaServiceImpl;
import pizzeria.domain.entities.Category;
import pizzeria.domain.entities.Pizza;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class UpdateMenuController {

  @FXML private TextField addNameField;
  @FXML private TextField addPriceField;
  @FXML private TextField addIngredientsField;
  @FXML private ChoiceBox<String> addCategoryChoice;
  @FXML private ImageView addImageView;
  @FXML private Label addImageLabel;

  @FXML private ChoiceBox<String> editPizzaChoice;
  @FXML private TextField editNameField;
  @FXML private TextField editPriceField;
  @FXML private TextField editIngredientsField;
  @FXML private ChoiceBox<String> editCategoryChoice;
  @FXML private ImageView editImageView;
  @FXML private Label editImageLabel;

  private final PizzaService pizzaService = new PizzaServiceImpl();
  private File addImageFile;
  private File editImageFile;
  private Pizza selectedPizza;

  @FXML
  public void initialize() {
    loadCategories(addCategoryChoice);
    loadCategories(editCategoryChoice);
    loadPizzaChoices();
  }

  @FXML
  private void handleAddSelectImage() {
    File file = chooseImage();
    if (file != null) {
      addImageFile = file;
      setImage(addImageView, addImageLabel, file);
    }
  }

  @FXML
  private void handleEditSelectImage() {
    File file = chooseImage();
    if (file != null) {
      editImageFile = file;
      setImage(editImageView, editImageLabel, file);
    }
  }

  @FXML
  private void handleAddPizza() {
    String name = addNameField.getText().trim();
    String priceText = addPriceField.getText().trim();
    String ingredients = addIngredientsField.getText().trim();
    String categoryName = addCategoryChoice.getValue();

    if (!validate(name, priceText)) return;

    UUID categoryId = resolveCategoryId(categoryName);
    double price = Double.parseDouble(priceText);

    pizzaService.addNewPizza(name, price, categoryId, ingredients);
    if (addImageFile != null) saveImage(name, addImageFile);

    MainController.showToast("Піца \"" + name + "\" додана");
    clearAddForm();
    loadPizzaChoices();
  }

  @FXML
  private void handleAddCancel() {
    clearAddForm();
  }

  @FXML
  private void handleEditPizza() {
    if (selectedPizza == null) {
      MainController.showToast("Оберіть піцу для редагування", false);
      return;
    }

    String name = editNameField.getText().trim();
    String priceText = editPriceField.getText().trim();
    String ingredients = editIngredientsField.getText().trim();
    String categoryName = editCategoryChoice.getValue();

    if (!validate(name, priceText)) return;

    UUID categoryId = resolveCategoryId(categoryName);
    double price = Double.parseDouble(priceText);

    pizzaService.updatePizza(selectedPizza.getId(), name, price, categoryId, ingredients);
    if (editImageFile != null) saveImage(name, editImageFile);

    MainController.showToast("Піца \"" + name + "\" оновлена");
    clearEditForm();
    loadPizzaChoices();
  }

  @FXML
  private void handleDeletePizza() {
    if (selectedPizza == null) {
      MainController.showToast("Оберіть піцу для видалення", false);
      return;
    }

    pizzaService.deletePizza(selectedPizza.getId());
    MainController.showToast("Піца \"" + selectedPizza.getName() + "\" видалена");
    clearEditForm();
    loadPizzaChoices();
  }

  private boolean validate(String name, String priceText) {
    if (name.isBlank()) {
      MainController.showToast("Введіть назву піци", false);
      return false;
    }
    if (priceText.isBlank()) {
      MainController.showToast("Введіть ціну", false);
      return false;
    }
    try {
      double price = Double.parseDouble(priceText);
      if (price <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
      MainController.showToast("Ціна має бути числом більше 0", false);
      return false;
    }
    return true;
  }

  private UUID resolveCategoryId(String categoryName) {
    if (categoryName == null || categoryName.isEmpty() || "Без категорії".equals(categoryName)) return null;
    for (Category c : pizzaService.getAllCategories()) {
      if (c.getName().equals(categoryName)) return c.getId();
    }
    return null;
  }

  private void loadCategories(ChoiceBox<String> box) {
    box.getItems().clear();
    box.getItems().add("Без категорії");
    for (Category cat : pizzaService.getAllCategories()) {
      box.getItems().add(cat.getName());
    }
    box.setValue("Без категорії");
  }

  private void loadPizzaChoices() {
    Pizza previous = selectedPizza;
    editPizzaChoice.getItems().clear();
    List<Pizza> pizzas = pizzaService.getAllPizzas();
    for (Pizza p : pizzas) {
      editPizzaChoice.getItems().add(p.getName() + "  —  " + String.format("%.0f грн", p.getPrice()));
    }
    editPizzaChoice.setOnAction(e -> onPizzaSelected());
    if (previous != null) {
      int idx = -1;
      for (int i = 0; i < pizzas.size(); i++) {
        if (pizzas.get(i).getId().equals(previous.getId())) { idx = i; break; }
      }
      if (idx >= 0) {
        editPizzaChoice.setValue(editPizzaChoice.getItems().get(idx));
        selectedPizza = pizzas.get(idx);
      } else {
        selectedPizza = null;
        clearEditForm();
      }
    }
  }

  private void onPizzaSelected() {
    int idx = editPizzaChoice.getSelectionModel().getSelectedIndex();
    List<Pizza> pizzas = pizzaService.getAllPizzas();
    if (idx >= 0 && idx < pizzas.size()) {
      selectedPizza = pizzas.get(idx);
      Pizza p = selectedPizza;
      editNameField.setText(p.getName());
      editPriceField.setText(String.valueOf(p.getPrice()));
      editIngredientsField.setText(p.getIngredients());
      loadCategories(editCategoryChoice);
      for (Category cat : pizzaService.getAllCategories()) {
        if (cat.getId().equals(p.getCategoryId())) {
          editCategoryChoice.setValue(cat.getName());
          break;
        }
      }
      editImageFile = null;
      editImageView.setImage(null);
      editImageView.setVisible(false);
      editImageLabel.setVisible(true);
    }
  }

  private void clearAddForm() {
    addNameField.clear();
    addPriceField.clear();
    addIngredientsField.clear();
    addImageView.setImage(null);
    addImageView.setVisible(false);
    addImageLabel.setVisible(true);
    addImageFile = null;
    loadCategories(addCategoryChoice);
  }

  private void clearEditForm() {
    selectedPizza = null;
    editPizzaChoice.setValue(null);
    editNameField.clear();
    editPriceField.clear();
    editIngredientsField.clear();
    editImageView.setImage(null);
    editImageView.setVisible(false);
    editImageLabel.setVisible(true);
    editImageFile = null;
    loadCategories(editCategoryChoice);
  }

  private File chooseImage() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Обрати фото піци");
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Зображення", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    return chooser.showOpenDialog(addImageView.getScene().getWindow());
  }

  private void setImage(ImageView iv, Label lbl, File file) {
    Image img = new Image(file.toURI().toString(), 120, 80, false, true);
    iv.setImage(img);
    iv.setVisible(true);
    lbl.setVisible(false);
  }

  private void saveImage(String pizzaName, File source) {
    String uploadDir = System.getProperty("user.home")
        + File.separator + ".urpizza" + File.separator + "images";
    try {
      Files.createDirectories(Path.of(uploadDir));
      String name = source.getName();
      int dot = name.lastIndexOf('.');
      String ext = (dot > 0) ? name.substring(dot) : ".jpg";
      String filename = pizzaName.replaceAll("[^a-zA-Zа-яА-Я0-9]", "_") + ext;
      Path dest = Path.of(uploadDir, filename);
      Files.copy(source.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
      AdminController.registerCustomImage(pizzaName, filename);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save pizza image", e);
    }
  }
}
