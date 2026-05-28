package pizzeria.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
  private PasswordUtil() {}

  public static boolean matches(String rawPassword, String storedPassword) {
    if (storedPassword == null) return false;
    try {
      if (storedPassword.startsWith("$2")) {
        return BCrypt.checkpw(rawPassword, storedPassword);
      }
    } catch (IllegalArgumentException ignored) {
      return false;
    }
    return storedPassword.equals(rawPassword);
  }

  public static String hash(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}
