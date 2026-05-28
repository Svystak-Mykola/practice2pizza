package pizzeria.domain.enums;

public enum PizzaSize {
  S,
  M,
  L;

  public static PizzaSize fromString(String value) {
    if (value == null) return M;
    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return M;
    }
  }

  public double getPriceMultiplier() {
    return switch (this) {
      case S -> 0.8;
      case M -> 1.0;
      case L -> 1.5;
    };
  }
}
