package murraco.repository;

import murraco.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);

}
