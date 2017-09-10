package storyboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import storyboard.domain.Action;

/**
 * Created by zachjustice on 7/30/17.
 */
@Repository("actionRepository")
public interface ActionRepository extends JpaRepository<Action, Long> {
    Action findActionById(int id);
}
