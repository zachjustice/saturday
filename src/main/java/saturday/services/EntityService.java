package saturday.services;

import saturday.domain.Entity;
import saturday.exceptions.BusinessLogicException;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface EntityService {
    Entity findEntityByEmail(String email) throws BusinessLogicException;
    Entity findEntityById(int id);

    Entity saveEntity(Entity entity) throws BusinessLogicException;
    Entity getAuthenticatedEntity();
}
