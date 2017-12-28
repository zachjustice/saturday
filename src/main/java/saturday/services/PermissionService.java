package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saturday.domain.*;
import saturday.exceptions.BusinessLogicException;

@Component
public class PermissionService {
    private final EntityService entityService;
    private final TopicMemberService topicMemberService;
    private final TopicService topicService;

    @Value("${saturday.topic.invite.status.pending}")
    private int TOPIC_MEMBER_STATUS_PENDING;
    @Value("${saturday.topic.invite.status.accepted}")
    private int TOPIC_MEMBER_STATUS_ACCEPTED;
    @Value("${saturday.topic.invite.status.rejected}")
    private int TOPIC_MEMBER_STATUS_REJECTED;
    @Value("${saturday.topic.invite.status.rescinded}")
    private int TOPIC_MEMBER_STATUS_RESCINDED;
    @Value("${saturday.topic.invite.status.left_topic}")
    private int TOPIC_MEMBER_STATUS_LEFT_TOPIC;

    @Autowired
    public PermissionService(EntityService entityService, TopicMemberService topicMemberService, TopicService topicService) {
        this.entityService = entityService;
        this.topicMemberService = topicMemberService;
        this.topicService = topicService;
    }

    /**
     * Check if the given entity is a member of the topic
     * @param entity The entity to check for membership
     * @param topic The topic
     * @return If the entity is a topic member
     */
    private boolean isTopicMember(Entity entity, Topic topic) throws BusinessLogicException {
        if(entity == null) {
            throw new BusinessLogicException("Could not authenticate permissions. Entity is null while checking isTopicMember().");
        }

        if(topic == null) {
            throw new BusinessLogicException("Could not authenticate permissions. Topic is null while checking isTopicMember().");
        }

        // TODO better way to do this
        TopicMemberStatus acceptedStatus = new TopicMemberStatus();
        acceptedStatus.setId(TOPIC_MEMBER_STATUS_ACCEPTED);

        TopicMember topicMember = this.topicMemberService.findByEntityAndTopicAndStatus(entity, topic, acceptedStatus);
        return topicMember != null;
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
     * Check if the auth'ed entity can send invites for a topic
     * TODO only topic moderators
     * @param topicMember The resource to validate
     * @return Whether the user is allowed to create the topic member
     */
    public boolean canCreate(TopicMember topicMember) throws BusinessLogicException {
        if(topicMember == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Topic Member is null while checking canCreate(TopicMember).");
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || isTopicMember(authenticatedEntity, topicMember.getTopic());
    }

    /**
     * Only members of the topic and admins can access associated topic content
     * @param topicContent The topic content to perform access validation against
     * @return whether or not the currently auth'ed entity can access this content
     */
    public boolean canView(TopicContent topicContent) throws BusinessLogicException {
        if(topicContent == null) {
            throw new BusinessLogicException("Null topic content argument while checking permissions.");
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();

        return authenticatedEntity.isAdmin()
                || isTopicMember(authenticatedEntity, topicContent.getTopic());
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
     * Only admins and topic members can view topic info
     * @param topic The topic to validate auth'ed user against
     * @return If the auth'ed user has access to the topic
     */
    public boolean canView(Topic topic) throws BusinessLogicException {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();

        return authenticatedEntity.isAdmin()
                || isTopicMember(authenticatedEntity, topic);
    }

    /**
     * Only admins and topic members can view topic info, i.e. who else is in the topic
     * @param topicMember The topic to validate auth'ed user against
     * @return If the auth'ed user has access to the topic
     */
    public boolean canView(TopicMember topicMember) throws BusinessLogicException {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();

        return authenticatedEntity.isAdmin()
                || topicMember.getEntity().getId() == authenticatedEntity.getId()
                || isTopicMember(authenticatedEntity, topicMember.getTopic());
    }

    /**
     * Topic members can create topic content
     * @param topicContentRequest TopicContentRequest to auth against
     * @return If the auth'ed user has access to the topic
     */
    public boolean canCreate(TopicContentRequest topicContentRequest) throws BusinessLogicException {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        // TODO better way to do this
        Topic topic = new Topic();
        topic.setId(topicContentRequest.getTopicId());

        return authenticatedEntity.isAdmin()
                || isTopicMember(authenticatedEntity, topic);
    }

    /**
     * Only topic creators can update topic details for now
     * TODO add moderator check
     * @param topic The topic to auth against
     * @return If the auth'ed user can modify the topic
     */
    public boolean canModify(Topic topic) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == topic.getCreator().getId();
    }

    /**
     * Only site admins or topic creators can delete topics
     * @param topic the topic to delete
     * @return If the auth'ed user can delete the topic
     */
    public boolean canDelete(Topic topic) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin()
                || authenticatedEntity.getId() == topic.getCreator().getId();
    }

    /**
     * Validates if the auth'ed entity can update the topic member
     * Currently, the only field on topic members that can be updated is
     * the topic member status.
     * Valid transitions for the topic member status
     * (pending|accepted) -> rescinded               by the inviter
     * rescinded          -> pending                 by the inviter
     * pending            -> (accepted | rejected)   by the invitee
     * accepted           -> left_topic              by the invitee
     // TODO topic moderators, etc can rescind invites
     * @param oldTopicMember The old topic member
     * @param newTopicMember The new topic member object with the applied updates
     * @return If the auth'ed user can modify the topic member
     * @throws BusinessLogicException if any of the arguments have null (required) fields
     */
    public boolean canModify(TopicMember oldTopicMember, TopicMember newTopicMember) throws BusinessLogicException {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        if(authenticatedEntity.isAdmin()) {
            return authenticatedEntity.isAdmin();
        }

        if(oldTopicMember.getStatus() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null topic member status.");
        }

        if(newTopicMember.getStatus() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null topic member status.");
        }

        if(oldTopicMember.getCreator() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null creator.");
        }

        if(oldTopicMember.getEntity() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null topic member entity.");
        }

        if(oldTopicMember.getStatus().getId() == newTopicMember.getStatus().getId()) {
            // If there was no change to the topic member status, then we're all good
            return true;
        } else if(oldTopicMember.getStatus().getId()          == TOPIC_MEMBER_STATUS_PENDING
                && newTopicMember.getStatus().getId()  == TOPIC_MEMBER_STATUS_RESCINDED
                && oldTopicMember.getCreator().getId() == authenticatedEntity.getId()) {

            // Validate pending -> rescinded transition
            //    you sent the invite, but want to take it back
            //    auth'ed entity must be creator
            return true;
        } else if(oldTopicMember.getStatus().getId()       == TOPIC_MEMBER_STATUS_ACCEPTED
                    && newTopicMember.getStatus().getId()  == TOPIC_MEMBER_STATUS_RESCINDED
                    && oldTopicMember.getCreator().getId() == authenticatedEntity.getId()) {

            // Validate pending -> rescinded transition
            //    someone accepted an invite, but you want to remove them
            //    auth'ed entity must be creator
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TOPIC_MEMBER_STATUS_PENDING
                && newTopicMember.getStatus().getId()  == TOPIC_MEMBER_STATUS_ACCEPTED
                && oldTopicMember.getEntity().getId()  == authenticatedEntity.getId()) {
            // Validate pending -> accepted transition
            //    you send an invite, and someone accepts
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TOPIC_MEMBER_STATUS_PENDING
                && newTopicMember.getStatus().getId()  == TOPIC_MEMBER_STATUS_REJECTED
                && oldTopicMember.getEntity().getId()  == authenticatedEntity.getId()) {
            // Validate pending -> rejected transition
            //    you send an invite, and they reject it
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TOPIC_MEMBER_STATUS_ACCEPTED
                && newTopicMember.getStatus().getId()  == TOPIC_MEMBER_STATUS_LEFT_TOPIC
                && oldTopicMember.getEntity().getId()  == authenticatedEntity.getId()) {
            // Validate accepted -> left transition
            //    someone accepts an invite but later leaves the group
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TOPIC_MEMBER_STATUS_RESCINDED
                && newTopicMember.getStatus().getId()  == TOPIC_MEMBER_STATUS_PENDING
                && oldTopicMember.getCreator().getId()  == authenticatedEntity.getId()) {
            // Validate rescinded -> pending left transition
            //    you rescind an invite, but then change your mind again
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TOPIC_MEMBER_STATUS_REJECTED
                && newTopicMember.getStatus().getId()  == TOPIC_MEMBER_STATUS_ACCEPTED
                && oldTopicMember.getEntity().getId()  == authenticatedEntity.getId()) {
            // Validate rejected -> accepted transition
            //    you reject an invite, but change your mind
            //    auth'ed entity must be future member
            return true;
        } else {
            // checked allowable transitions
            // everything else is not allowed
            return false;
        }
    }
}
