package pizzeria.infrastructure.persistence.contract;

import pizzeria.domain.entities.User;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findByEmail(String email);

  void save(User user);
}
