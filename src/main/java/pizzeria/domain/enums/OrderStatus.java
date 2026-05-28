package pizzeria.domain.enums;

public enum OrderStatus {
  NEW,
  IN_PROGRESS,
  READY,
  ON_THE_WAY,
  DELIVERED;

  public static OrderStatus fromString(String value) {
    if (value == null) return NEW;
    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return NEW;
    }
  }

  public String displayName() {
    return switch (this) {
      case NEW -> "НОВЕ";
      case IN_PROGRESS -> "В РОБОТІ";
      case READY -> "ГОТОВО";
      case ON_THE_WAY -> "В ДОСТАВЦІ";
      case DELIVERED -> "ДОСТАВЛЕНО";
    };
  }
}
