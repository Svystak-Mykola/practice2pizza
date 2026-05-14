package pizzeria.application.impl;

import pizzeria.application.contract.CartService;
import pizzeria.domain.entities.Pizza;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartServiceImpl implements CartService {

  private final Map<Pizza, Integer> items = new HashMap<>();
  private static final Path SETTINGS_FILE = Path.of("pizzeria-cart.settings");
  private static final Path CART_FILE = Path.of("pizzeria-cart.tsv");
  private static boolean persistEnabled = loadPersistEnabled();

  public CartServiceImpl() {
    loadCart();
  }

  @Override
  public void addPizza(Pizza pizza) {

    if (pizza == null) {
      return;
    }

    items.put(pizza, items.getOrDefault(pizza, 0) + 1);
    saveCart();
  }

  @Override
  public void removePizza(Pizza pizza) {

    if (!items.containsKey(pizza)) {
      return;
    }

    int quantity = items.get(pizza);

    if (quantity <= 1) {
      items.remove(pizza);
    } else {
      items.put(pizza, quantity - 1);
    }
    saveCart();
  }

  @Override
  public Map<Pizza, Integer> getItems() {
    return items;
  }

  @Override
  public double getTotal() {

    return items.entrySet()
        .stream()
        .mapToDouble(entry ->
            entry.getKey().getPrice() * entry.getValue())
        .sum();
  }

  @Override
  public void clear() {
    items.clear();
    saveCart();
  }

  public static boolean isPersistEnabled() {
    return persistEnabled;
  }

  public static void setPersistEnabled(boolean enabled) {
    persistEnabled = enabled;
    try {
      Files.writeString(SETTINGS_FILE, Boolean.toString(enabled), StandardCharsets.UTF_8);
      if (!enabled) {
        Files.deleteIfExists(CART_FILE);
      }
    } catch (IOException ignored) {
    }
  }

  public void persistCurrentCart() {
    saveCart();
  }

  private static boolean loadPersistEnabled() {
    try {
      if (Files.exists(SETTINGS_FILE)) {
        return Boolean.parseBoolean(Files.readString(SETTINGS_FILE, StandardCharsets.UTF_8).trim());
      }
    } catch (IOException ignored) {
    }
    return false;
  }

  private void saveCart() {
    if (!persistEnabled) return;
    try {
      StringBuilder out = new StringBuilder();
      for (Map.Entry<Pizza, Integer> entry : items.entrySet()) {
        Pizza pizza = entry.getKey();
        out.append(pizza.getId()).append('\t')
            .append(pizza.getCategoryId()).append('\t')
            .append(encode(pizza.getCategoryName())).append('\t')
            .append(encode(pizza.getName())).append('\t')
            .append(pizza.getPrice()).append('\t')
            .append(entry.getValue()).append('\n');
      }
      Files.writeString(CART_FILE, out.toString(), StandardCharsets.UTF_8);
    } catch (IOException ignored) {
    }
  }

  private void loadCart() {
    if (!persistEnabled || !Files.exists(CART_FILE)) return;
    try {
      List<String> lines = Files.readAllLines(CART_FILE, StandardCharsets.UTF_8);
      for (String line : lines) {
        if (line.isBlank()) continue;
        String[] parts = line.split("\t");
        if (parts.length != 6) continue;
        Pizza pizza = new Pizza(
            UUID.fromString(parts[0]),
            UUID.fromString(parts[1]),
            decode(parts[2]),
            decode(parts[3]),
            Double.parseDouble(parts[4])
        );
        int quantity = Integer.parseInt(parts[5]);
        if (quantity > 0) {
          items.put(pizza, quantity);
        }
      }
    } catch (Exception ignored) {
      items.clear();
    }
  }

  private static String encode(String value) {
    String safe = value == null ? "" : value;
    return Base64.getEncoder().encodeToString(safe.getBytes(StandardCharsets.UTF_8));
  }

  private static String decode(String value) {
    return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
  }
}
