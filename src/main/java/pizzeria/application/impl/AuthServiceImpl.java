package pizzeria.application.impl;

import pizzeria.application.contract.AuthService;
import pizzeria.domain.entities.User;
import pizzeria.infrastructure.persistence.contract.UserRepository;
import pizzeria.infrastructure.persistence.impl.UserRepositoryImpl;
import pizzeria.util.PasswordUtil;

public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;

  public AuthServiceImpl() {
    this.userRepository = new UserRepositoryImpl();
  }

  @Override
  public User login(String email, String password) {
    String normalizedEmail = email == null ? "" : email.trim();
    String rawPassword = password == null ? "" : password;

    if (normalizedEmail.isBlank() || rawPassword.isBlank()) {
      throw new RuntimeException("Заповни email і пароль.");
    }

    User user = userRepository.findByEmail(normalizedEmail)
        .orElseThrow(() -> new RuntimeException("Користувача з такою електронною поштою не знайдено."));

    if (!PasswordUtil.matches(rawPassword, user.getPassword())) {
      throw new RuntimeException("Введено неправильний пароль.");
    }
    return user;
  }

  @Override
  public void register(String name, String email, String password) {
    register(name, email, password, null);
  }

  public void register(String name, String email, String password, String phone) {
    String normalizedEmail = email == null ? "" : email.trim();
    if (name == null || name.trim().isBlank() || normalizedEmail.isBlank() || password == null || password.length() < 6) {
      throw new RuntimeException("Перевір ім'я, email і пароль.");
    }
    if (phone != null && !phone.isBlank() && !phone.matches("^\\+?\\d{10,13}$")) {
      throw new RuntimeException("Телефон має містити 10-13 цифр.");
    }
    if (userRepository.findByEmail(normalizedEmail).isPresent()) {
      throw new RuntimeException("Дана електронна пошта вже використовується.");
    }

    String normalizedPhone = phone == null || phone.isBlank() ? null : phone.trim();
    String hashedPassword = PasswordUtil.hash(password);
    User newUser = new User(java.util.UUID.randomUUID(), name.trim(), normalizedEmail, hashedPassword, null, normalizedPhone, pizzeria.domain.enums.Role.USER);
    userRepository.save(newUser);
  }

}
