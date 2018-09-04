package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> {
    Entity findByEmail(String email);
    Entity findById(int id);

    @Query(
            value = "select * from entities where LOWER(email) LIKE CONCAT('%', LOWER(:term), '%') or LOWER(name) LIKE CONCAT('%', LOWER(:term), '%') limit 20",
            nativeQuery = true
    )
    List<Entity> search(@Param("term") String term);
}
