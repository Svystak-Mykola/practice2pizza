package pizzeria.domain.enums;

public enum Role {
  USER,
  ADMIN,
  CHEF,
  COURIER;

  public static Role fromString(String value) {
    if (value == null) return USER;
    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return USER;
    }
  }

  public String displayName() {
    return switch (this) {
      case USER -> "Користувач";
      case ADMIN -> "Адміністратор";
      case CHEF -> "Кухар";
      case COURIER -> "Кур'єр";
    };
  }
}
