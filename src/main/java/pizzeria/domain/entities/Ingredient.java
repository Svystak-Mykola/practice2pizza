package pizzeria.domain.entities;

import java.util.UUID;

public class Ingredient {

  private UUID id;
  private String name;

  public Ingredient() {}

  public Ingredient(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  @Override
  public String toString() {
    return name;
  }
}
