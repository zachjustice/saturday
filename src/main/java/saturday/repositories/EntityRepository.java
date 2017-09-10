package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> {
    Entity findByEmail(String email);
    Entity findById(int id);
}
