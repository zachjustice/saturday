package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.TopicContent;
import saturday.domain.TopicInvite;
import saturday.domain.TopicMemberRequest;

@Component
public class PermissionService {
    private final EntityService entityService;

    @Autowired
    public PermissionService(EntityService entityService) {
        this.entityService = entityService;
    }

    public boolean canAccess(Entity entity) {
        if(entity == null) {
            // TODO throw exception?
           return false;
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == entity.getId();
    }

    // TODO for now only entities who can add a topicMember via post route
    public boolean canAccess(TopicMemberRequest topicMemberRequest) {
        if(topicMemberRequest == null) {
            return false;
        }
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin();
    }

    public boolean canAcceptInvite(TopicInvite topicInvite) {
        if(topicInvite == null) {
            return false;
        }
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicInvite.getInvitee().getId();
    }

    public boolean canDeleteInvite(TopicInvite topicInvite) {
        if(topicInvite == null) {
            return false;
        }
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicInvite.getInviter().getId();
    }

    public boolean canRejectInvite(TopicInvite topicInvite) {
        if(topicInvite == null) {
            return false;
        }
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicInvite.getInvitee().getId();
    }

    public boolean canModify(TopicContent topicContent) {
        if(topicContent == null) {
            return false;
        }
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicContent.getCreator().getId();
    }

    // TODO topic admins / moderators
    public boolean canDelete(TopicContent topicContent) {
        if(topicContent == null) {
            return false;
        }
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topicContent.getCreator().getId();
    }
}
