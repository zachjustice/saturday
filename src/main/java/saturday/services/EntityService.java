package saturday.services;

import saturday.domain.Entity;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface EntityService {
    Entity findEntityByEmail(String email) ;
    Entity findEntityById(int id) ;

    Entity updateEntity(Entity updatedEntity);
    Entity updateEntity(Entity updatedEntity, boolean setPassword);
    Entity saveEntity(Entity entity) ;
    Entity getAuthenticatedEntity();
}
