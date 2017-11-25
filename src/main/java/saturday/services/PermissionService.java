package saturday.services;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;

@Component
public class PermissionService {
    private Entity authenticatedEntity;

    private final EntityService entityService;

    public PermissionService(EntityService entityService) {
        this.entityService = entityService;
    }


    private Entity getAuthenticatedEntity() {
        if(authenticatedEntity == null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

            try {
                authenticatedEntity = entityService.findEntityByEmail(email);
            } catch (EmptyResultDataAccessException ex) {
                throw new RequestRejectedException("Couldn't find email for authenticated entity: '" + email + "'");
            }
        }

        return authenticatedEntity;
    }

    public boolean canAccess(int resourceId) {
        return resourceId == getAuthenticatedEntity().getId();
    }
}
