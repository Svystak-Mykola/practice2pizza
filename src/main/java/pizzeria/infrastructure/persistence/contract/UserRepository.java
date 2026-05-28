package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  List<User> findAll();

  Optional<User> findByEmail(String email);

  void save(User user);
}
