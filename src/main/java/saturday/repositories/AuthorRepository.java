package storyboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import storyboard.domain.Author;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository("authorRepository")
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByEmail(String email);
    Author findById(int id);
}
