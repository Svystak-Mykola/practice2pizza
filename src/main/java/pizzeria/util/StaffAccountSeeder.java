package pizzeria.util;

import org.mindrot.jbcrypt.BCrypt;
import pizzeria.domain.entities.User;
import pizzeria.domain.enums.Role;
import pizzeria.infrastructure.persistence.impl.UserRepositoryImpl;

import java.util.UUID;

public final class StaffAccountSeeder {

  private StaffAccountSeeder() {}

  public static void seed() {
    UserRepositoryImpl users = new UserRepositoryImpl();
    String password = System.getenv().getOrDefault("URPIZZA_STAFF_PASSWORD", "123456");
    seedRole(users, "ADMIN", "Адміністратор", "URPIZZA_ADMIN_EMAILS", "admin@urpizza.local", password);
    seedRole(users, "CHEF", "Кухар", "URPIZZA_CHEF_EMAILS", "chef@urpizza.local", password);
    seedRole(users, "COURIER", "Кур'єр", "URPIZZA_COURIER_EMAILS", "courier@urpizza.local", password);
  }

  private static void seedRole(UserRepositoryImpl users, String roleStr, String name, String envName,
                                String defaults, String password) {
    Role role = Role.fromString(roleStr);
    for (String email : RoleResolver.emailsFor(envName, defaults)) {
      User user = users.findByEmail(email).orElseGet(() ->
          new User(UUID.randomUUID(), name, email, BCrypt.hashpw(password, BCrypt.gensalt())));
      user.setRole(role);
      users.save(user);
    }
  }
}
