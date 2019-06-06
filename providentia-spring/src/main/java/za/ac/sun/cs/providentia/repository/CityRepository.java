package za.ac.sun.cs.providentia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.sun.cs.providentia.domain.City;

import java.util.UUID;

/**
 * Spring Data repository for the City entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
}
