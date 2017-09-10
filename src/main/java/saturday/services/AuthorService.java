package storyboard.services;

import storyboard.domain.Author;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface AuthorService {
    Author findAuthorByEmail(String email);
    Author findAuthorById(int id);

    void saveAuthor(Author author);
}
