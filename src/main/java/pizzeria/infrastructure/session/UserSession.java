package pizzeria.infrastructure.session;

import pizzeria.domain.entities.User;

public class  UserSession {

  private static User currentUser;

  public static void setCurrentUser(User user) {
    currentUser = user;
  }

  public static User getCurrentUser() {
    return currentUser;
  }

  public static void clear() {
    currentUser = null;
  }

  public static boolean isLoggedIn() {
    return currentUser != null;
  }
}
