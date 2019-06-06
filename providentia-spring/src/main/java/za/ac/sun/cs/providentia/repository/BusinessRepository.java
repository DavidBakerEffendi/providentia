package za.ac.sun.cs.providentia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.sun.cs.providentia.domain.Business;

/**
 * Spring Data repository for the Business entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BusinessRepository extends JpaRepository<Business, String> {
}
