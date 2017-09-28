package saturday.services;

import saturday.domain.Entity;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface EntityService {
    Entity findEntityByEmail(String email);
    Entity findEntityById(int id);

    void saveEntity(Entity entity);
}
