package pizzeria.domain.entities;

import java.util.UUID;
import java.util.Objects;

public class Pizza {

  private UUID id;
  private UUID categoryId;
  private String categoryName;
  private String name;
  private double price;

  public Pizza() {}

  public Pizza(UUID id, UUID categoryId, String categoryName, String name, double price) {
    this.id = id;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.name = name;
    this.price = price;
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
