package saturday.services;

import org.springframework.stereotype.Component;

@Component
public class PermissionService {
    private final EntityService entityService;

    public PermissionService(EntityService entityService) {
        this.entityService = entityService;
    }

    public boolean canAccess(int resourceId) {
        return resourceId == entityService.getAuthenticatedEntity().getId();
    }
}
