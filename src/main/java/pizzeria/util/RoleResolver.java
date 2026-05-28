package pizzeria.util;

import pizzeria.domain.enums.Role;
import java.util.Arrays;
import java.util.List;

public final class RoleResolver {

  private RoleResolver() {}

  public static Role resolve(String email) {
    String normalized = normalize(email);
    if (matches(normalized, "URPIZZA_ADMIN_EMAILS", "admin@urpizza.local")) return Role.ADMIN;
    if (matches(normalized, "URPIZZA_CHEF_EMAILS", "chef@urpizza.local")) return Role.CHEF;
    if (matches(normalized, "URPIZZA_COURIER_EMAILS", "courier@urpizza.local")) return Role.COURIER;
    return Role.USER;
  }

  public static List<String> emailsFor(String envName, String defaults) {
    String configured = System.getenv().getOrDefault(envName, defaults);
    return Arrays.stream(configured.split(","))
        .map(RoleResolver::normalize)
        .filter(value -> !value.isBlank())
        .toList();
  }

  private static boolean matches(String email, String envName, String defaults) {
    return emailsFor(envName, defaults).stream().anyMatch(email::equals);
  }

  private static String normalize(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }
}
