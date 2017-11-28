package saturday.services;

import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.TopicInvite;
import saturday.domain.TopicMemberRequest;

@Component
public class PermissionService {
    private final EntityService entityService;

    public PermissionService(EntityService entityService) {
        this.entityService = entityService;
    }

    public boolean canAccess(Entity entity) {
        if(entity == null) {
            // TODO throw exception?
           return false;
        }

        Entity authenticatedEntity = entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == entity.getId();
    }

    // TODO for now only entities who can add a topicMember via post route
    public boolean canAccess(TopicMemberRequest topicMemberRequest) {
        Entity authenticatedEntity = entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin();
    }

    public boolean canAcceptInvite(TopicInvite topicInvite) {
        Entity authenticatedEntity = entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicInvite.getInvitee().getId();
    }

    public boolean canDeleteInvite(TopicInvite topicInvite) {
        Entity authenticatedEntity = entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicInvite.getInviter().getId();
    }

    public boolean canRejectInvite(TopicInvite topicInvite) {
        Entity authenticatedEntity = entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicInvite.getInvitee().getId();
    }
}
