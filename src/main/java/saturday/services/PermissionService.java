package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.*;

import java.util.List;

@Component
public class PermissionService {
    private final EntityService entityService;
    private final TopicMemberService topicMemberService;

    @Autowired
    public PermissionService(EntityService entityService, TopicMemberService topicMemberService) {
        this.entityService = entityService;
        this.topicMemberService = topicMemberService;
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

    /**
     * Only members of the topic and admins can access associated topic content
     * @param topicContent The topic content to perform access validation against
     * @return whether or not the currently auth'ed entity can access this content
     */
    public boolean canAccess(TopicContent topicContent) {
        if(topicContent == null) {
            // TODO throw exception?
            return false;
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        List<TopicMember> topicMemberList = this.topicMemberService.findByTopicId(topicContent.getId());
        boolean isAuthenticatedEntityAMember = topicMemberList.stream()
                        .anyMatch(topicMember -> {
                            return topicMember.getEntity().getId() == authenticatedEntity.getId();
                        });

        return authenticatedEntity.isAdmin() || isAuthenticatedEntityAMember;
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
