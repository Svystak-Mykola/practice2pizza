package pizzeria.application.impl;

import org.mindrot.jbcrypt.BCrypt;
import pizzeria.application.contract.AuthService;
import pizzeria.domain.entities.User;
import pizzeria.infrastructure.persistence.contract.UserRepository;
import pizzeria.infrastructure.persistence.impl.UserRepositoryImpl;

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

    if (!passwordMatches(rawPassword, user.getPassword())) {
      throw new RuntimeException("Введено неправильний пароль.");
    }
    return user;
  }

  @Override
  public void register(String name, String email, String password) {
    String normalizedEmail = email == null ? "" : email.trim();
    if (name == null || name.trim().isBlank() || normalizedEmail.isBlank() || password == null || password.length() < 6) {
      throw new RuntimeException("Перевір ім'я, email і пароль.");
    }
    if (userRepository.findByEmail(normalizedEmail).isPresent()) {
      throw new RuntimeException("Дана електронна пошта вже використовується.");
    }

    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    User newUser = new User(java.util.UUID.randomUUID(), name.trim(), normalizedEmail, hashedPassword);
    userRepository.save(newUser);
  }

  private boolean passwordMatches(String rawPassword, String storedPassword) {
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
}
