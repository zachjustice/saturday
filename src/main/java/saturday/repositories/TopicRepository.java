package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.Topic;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Topic findByName(String name);
    Topic findById(int id);
}
