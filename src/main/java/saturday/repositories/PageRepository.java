package storyboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import storyboard.domain.Page;

/**
 * Created by zachjustice on 7/29/17.
 */
@Repository("pageRepository")
public interface PageRepository extends JpaRepository<Page, Long> {
    Page findById(int id);
}
