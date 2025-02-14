package shopmate.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shopmate.userservice.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}