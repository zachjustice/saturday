package saturday.services;

import saturday.domain.Entity;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.ResourceNotFoundException;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface EntityService {
    Entity findEntityByEmail(String email) throws BusinessLogicException;
    Entity findEntityById(int id) throws ResourceNotFoundException;

    Entity updateEntity(Entity updatedEntity) throws BusinessLogicException, ProcessingResourceException, ResourceNotFoundException;
    Entity saveEntity(Entity entity) throws BusinessLogicException;
    Entity getAuthenticatedEntity();
}
