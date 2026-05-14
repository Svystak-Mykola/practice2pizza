package pizzeria.application.contract;

import pizzeria.domain.entities.User;

public interface AuthService {

  User login(String email, String password);

  void register(
      String name,
      String email,
      String password
  );
}
