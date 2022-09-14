package shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
