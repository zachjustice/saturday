package saturday.services;

import saturday.domain.Entity;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface AuthorService {
    Entity findAuthorByEmail(String email);
    Entity findAuthorById(int id);

    void saveAuthor(Entity entity);
}
