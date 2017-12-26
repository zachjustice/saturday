package saturday.services;

import saturday.domain.Entity;
import saturday.exceptions.ProcessingResourceException;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface EntityService {
    Entity findEntityByEmail(String email) throws ProcessingResourceException;
    Entity findEntityById(int id);

    Entity saveEntity(Entity entity) throws ProcessingResourceException;
    Entity getAuthenticatedEntity();
}
