package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.*;

import java.util.List;

@Component
public class PermissionService {
    private final EntityService entityService;
    private final TopicMemberService topicMemberService;
    private final TopicService topicService;

    @Autowired
    public PermissionService(EntityService entityService, TopicMemberService topicMemberService, TopicService topicService) {
        this.entityService = entityService;
        this.topicMemberService = topicMemberService;
        this.topicService = topicService;
    }

    public boolean canAccess(Entity entity) {
        if(entity == null) {
            // TODO throw exception?
           return false;
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == entity.getId();
    }

    /**
     * Check if the auth'ed entity can create the topic member resource
     * Only site admins can use this resource since normal users should use invites
     * @param topicMemberRequest The resource to validate
     * @return Whether the user is allowed to create the topic member
     */
    public boolean canCreate(TopicMemberRequest topicMemberRequest) {
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

    /**
     * Inviters, invitees, and admins can view topic invites.
     * TODO: Topic moderators, etc should be able to view a topic's invites
     *
     * @param topicInvite to valid access against
     * @return whether auth'ed entity can view this topic invite
     */
    public boolean canView(TopicInvite topicInvite) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin()
                || authenticatedEntity.getId() == topicInvite.getInvitee().getId()
                || authenticatedEntity.getId() == topicInvite.getInviter().getId();

    }

    /**
     * Topic members can send invites to other users.
     *
     * @param topicInviteRequest The topic invite to be sent
     * @return Whether or not the authenticated user can send the invite
     */
    public boolean canSendInvite(TopicInviteRequest topicInviteRequest) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        if (authenticatedEntity.isAdmin()) {
            return true;
        }

        //Topic topic = topicService.findTopicById(topicInviteRequest.getTopicId());
        Topic topic = topicService.findTopicById(topicInviteRequest.getTopicId());
        TopicMember topicMember = this.topicMemberService.findByEntityAndTopic(authenticatedEntity, topic);
        if (topicMember != null && topicMember.getEntity().getId() != authenticatedEntity.getId()) {
            return false;
        }

        return true;
    }
}
