package pizzeria.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import pizzeria.application.contract.CartService;
import pizzeria.domain.entities.OrderItem;
import pizzeria.domain.entities.Pizza;

import java.util.UUID;

public class SizePickerController {

  @FXML private Label pizzaNameLabel;
  @FXML private Label pizzaIngredientsLabel;
  @FXML private Button btnS, btnM, btnL;
  @FXML private Label priceS, priceM, priceL;
  @FXML private Label selectedSizeLabel, selectedPriceLabel;

  private Pizza pizza;
  private String selectedSize = "M";
  private Runnable onConfirm;
  private Runnable onCancel;

  public void setPizza(Pizza pizza) {
    this.pizza = pizza;
    pizzaNameLabel.setText(pizza.getName());
    pizzaIngredientsLabel.setText(pizza.getIngredientsText());
    updatePriceLabels();
    updateSelection();
  }

  public void setOnConfirm(Runnable r) { this.onConfirm = r; }
  public void setOnCancel(Runnable r)  { this.onCancel = r; }

  private void updatePriceLabels() {
    double base = pizza.getPrice();
    priceS.setText(String.format("%.0f грн", base * 0.8));
    priceM.setText(String.format("%.0f грн", base));
    priceL.setText(String.format("%.0f грн", base * 1.5));
  }

  private void updateSelection() {
    btnS.getStyleClass().remove("size-button-selected");
    btnM.getStyleClass().remove("size-button-selected");
    btnL.getStyleClass().remove("size-button-selected");

    double price = pizza.getPrice();
    switch (selectedSize) {
      case "S" -> { btnS.getStyleClass().add("size-button-selected"); price *= 0.8; }
      case "M" -> { btnM.getStyleClass().add("size-button-selected"); }
      case "L" -> { btnL.getStyleClass().add("size-button-selected"); price *= 1.5; }
    }

    selectedSizeLabel.setText("Розмір " + selectedSize);
    selectedPriceLabel.setText(String.format("%.0f грн", price));
  }

  @FXML private void selectS() { selectedSize = "S"; updateSelection(); }
  @FXML private void selectM() { selectedSize = "M"; updateSelection(); }
  @FXML private void selectL() { selectedSize = "L"; updateSelection(); }

  @FXML
  private void handleConfirm() {
    CartService cart = MainController.getCartService();

    double price = pizza.getPrice();
    if (selectedSize.equals("S")) price *= 0.8;
    if (selectedSize.equals("L")) price *= 1.5;

    Pizza cartPizza = new Pizza(pizza.getId(), pizza.getCategoryId(),
        pizza.getCategoryName(),
        pizza.getName() + " (" + selectedSize + ")", price);
    cart.addPizza(cartPizza);

    CartController.getInstance().refresh();

    if (onConfirm != null) onConfirm.run();
  }

  @FXML
  private void handleCancel() {
    if (onCancel != null) onCancel.run();
  }

  @FXML
  private void handleOverlayClick(MouseEvent e) {
    if (e.getTarget() instanceof StackPane) handleCancel();
  }

  @FXML
  private void consumeClick(MouseEvent e) {
    e.consume();
  }
}
