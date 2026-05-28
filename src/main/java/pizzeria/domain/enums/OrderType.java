package pizzeria.domain.enums;

public enum OrderType {
  DINE_IN,
  TAKEAWAY,
  DELIVERY;

  public static OrderType fromString(String value) {
    if (value == null) return DINE_IN;
    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return DINE_IN;
    }
  }

  public String displayName() {
    return switch (this) {
      case DINE_IN -> "В залі";
      case TAKEAWAY -> "Із собою";
      case DELIVERY -> "Доставка";
    };
  }
}
