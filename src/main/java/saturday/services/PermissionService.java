package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.*;
import saturday.exceptions.ProcessingResourceException;

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

    public boolean canView(Entity entity) {
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
    public boolean canView(TopicContent topicContent) throws ProcessingResourceException {
        if(topicContent == null) {
            throw new ProcessingResourceException("Null topic content argument while checking permissions.");
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();

        return authenticatedEntity.isAdmin()
                || isTopicMember(authenticatedEntity, topicContent.getTopic());
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

        //Topic topic = topicService.findTopicById(topicInviteRequest.getTopicId());
        Topic topic = topicService.findTopicById(topicInviteRequest.getTopicId());
        return authenticatedEntity.isAdmin() || isTopicMember(authenticatedEntity, topic);
    }

    /**
     * Only admins and topic members can view topic info
     * @param topic The topic to validate auth'ed user against
     * @return If the auth'ed user has access to the topic
     */
    public boolean canView(Topic topic) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();

        return authenticatedEntity.isAdmin()
                || isTopicMember(authenticatedEntity, topic);
    }

    /**
     * Only admins and topic members can view topic info, i.e. who else is in the topic
     * @param topicMember The topic to validate auth'ed user against
     * @return If the auth'ed user has access to the topic
     */
    public boolean canView(TopicMember topicMember) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();

        return authenticatedEntity.isAdmin()
                || isTopicMember(authenticatedEntity, topicMember.getTopic());
    }

    /**
     * Topic members can create topic content
     * @param topicContentRequest the topic content request to authenticate
     * @return If the auth'ed user has access to the topic
     */
    public boolean canCreate(TopicContentRequest topicContentRequest) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        // TODO better way to do this
        Topic topic = new Topic();
        topic.setId(topicContentRequest.getTopicId());

        return authenticatedEntity.isAdmin()
                || isTopicMember(authenticatedEntity, topic);
    }

    /**
     * Check if the given entity is a member of the topic
     * @param entity The entity to check for membership
     * @param topic The topic
     * @return If the entity is a topic member
     */
    private boolean isTopicMember(Entity entity, Topic topic) {
        TopicMember topicMember = this.topicMemberService.findByEntityAndTopic(entity, topic);
        return topicMember != null;
    }
}
