package za.ac.sun.cs.providentia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.sun.cs.providentia.domain.Review;

/**
 * Spring Data repository for the Review entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
}
