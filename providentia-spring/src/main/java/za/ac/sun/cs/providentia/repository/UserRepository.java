package za.ac.sun.cs.providentia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.sun.cs.providentia.domain.User;

/**
 * Spring Data repository for the User entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
