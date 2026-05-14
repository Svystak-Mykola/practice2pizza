package pizzeria.infrastructure.session;

import java.io.*;
import java.nio.file.*;

public class SessionStorage {

  private static final Path SESSION_FILE = Paths.get("pizzeria.session");

  public static void save(String userId) {
    try {
      Files.writeString(SESSION_FILE, userId);
    } catch (IOException e) {
      System.err.println("Не вдалось зберегти сесію: " + e.getMessage());
    }
  }

  public static String load() {
    try {
      if (Files.exists(SESSION_FILE)) {
        return Files.readString(SESSION_FILE).trim();
      }
    } catch (IOException e) {
      System.err.println("Не вдалось прочитати сесію: " + e.getMessage());
    }
    return null;
  }

  public static void clear() {
    try {
      Files.deleteIfExists(SESSION_FILE);
    } catch (IOException e) {
      System.err.println("Не вдалось видалити сесію: " + e.getMessage());
    }
  }
}
