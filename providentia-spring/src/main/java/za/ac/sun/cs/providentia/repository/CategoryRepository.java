package za.ac.sun.cs.providentia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.sun.cs.providentia.domain.Category;

import java.util.UUID;

/**
 * Spring Data repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
