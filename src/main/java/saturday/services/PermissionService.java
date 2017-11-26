package saturday.services;

import org.springframework.stereotype.Component;
import saturday.domain.Entity;

@Component
public class PermissionService {
    private final EntityService entityService;

    public PermissionService(EntityService entityService) {
        this.entityService = entityService;
    }

    public boolean canAccess(Entity entity) {
        if(entity == null) {
           return false;
        }

        Entity authenticatedEntity = entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || entityService.getAuthenticatedEntity().getId() == entity.getId();
    }
}
