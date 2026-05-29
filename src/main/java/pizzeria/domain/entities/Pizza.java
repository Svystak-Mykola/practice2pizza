package pizzeria.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

public class Pizza {

  private UUID id;
  private UUID categoryId;
  private String categoryName;
  private String name;
  private double price;
  private List<Ingredient> ingredients;

  public Pizza() {
    this.ingredients = new ArrayList<>();
  }

  public Pizza(UUID id, UUID categoryId, String categoryName, String name, double price, List<Ingredient> ingredients) {
    this.id = id;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.name = name;
    this.price = price;
    this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
  }

  public Pizza(UUID id, UUID categoryId, String categoryName, String name, double price) {
    this(id, categoryId, categoryName, name, price, new ArrayList<>());
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getCategoryId() { return categoryId; }
  public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
  public String getCategoryName() { return categoryName; }
  public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public double getPrice() { return price; }
  public void setPrice(double price) { this.price = price; }
  public List<Ingredient> getIngredients() { return ingredients; }
  public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
  public String getIngredientsText() {
    if (ingredients == null || ingredients.isEmpty()) return "";
    return ingredients.stream().map(Ingredient::getName).reduce((a, b) -> a + ", " + b).orElse("");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Pizza other)) return false;
    return Double.compare(other.price, price) == 0
        && Objects.equals(id, other.id)
        && Objects.equals(name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, price);
  }

  @Override
  public String toString() {
    return name + " - " + price + " грн";
  }
}
