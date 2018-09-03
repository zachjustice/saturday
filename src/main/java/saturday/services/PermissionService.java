package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.domain.TopicMember;
import saturday.domain.topicPermissions.TopicPermission;
import saturday.domain.TopicRolePermission;
import saturday.domain.topicMemberStatuses.TopicMemberStatus;
import saturday.domain.topicMemberStatuses.TopicMemberStatusAccepted;
import saturday.domain.topicRoles.TopicRole;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.ResourceNotFoundException;

@Component
public class PermissionService {
    private final EntityService entityService;
    private final TopicMemberService topicMemberService;
    private final TopicRolePermissionService topicRolePermissionService;
    private final TopicService topicService;

    @Autowired
    public PermissionService(EntityService entityService, TopicMemberService topicMemberService, TopicRolePermissionService topicRolePermissionService, TopicService topicService) {
        this.entityService = entityService;
        this.topicMemberService = topicMemberService;
        this.topicRolePermissionService = topicRolePermissionService;
        this.topicService = topicService;
    }

    /**
     * Check if the given entity is a member of the topic
     * @param entity The entity to check for membership
     * @param topic The topic
     * @return If the entity is a topic member
     */
    private boolean isTopicMember(Entity entity, Topic topic) {
        if(entity == null) {
            throw new BusinessLogicException("Could not authenticate permissions. Entity is null while checking isTopicMember().");
        }

        if(topic == null) {
            throw new BusinessLogicException("Could not authenticate permissions. Topic is null while checking isTopicMember().");
        }

        TopicMemberStatus acceptedStatus = new TopicMemberStatusAccepted();

        TopicMember topicMember = this.topicMemberService.findByEntityAndTopicAndStatus(entity, topic, acceptedStatus);
        return topicMember != null;
    }

    private boolean isTopicMemberAllowed(int topicId, int topicRoleId, int topicPermissionId) {
        TopicRolePermission topicRolePermission = this.topicRolePermissionService.findByTopicIdAndTopicRoleIdAndTopicPermissionId(
                topicId,
                topicRoleId,
                topicPermissionId
        );

        return topicRolePermission != null && topicRolePermission.getIsAllowed();
    }

    /**
     * Check if the auth'ed entity can access an Entity owned resource.
     * This check applies to actions for updating the entity, sending confirmation emails, etc.
     * @param entity The entity to auth against
     * @return If the entity is allowed access
     */
    public boolean canView(Entity entity) {
        if(entity == null) {
            throw new IllegalArgumentException(
                    "Failed to authenticate permissions. " +
                            "Entity is null while checking canAccess(Entity)"
            );
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin()
                || entity.getIsProfilePublic()
                || topicMemberService.isTopicMembersTogether(authenticatedEntity, entity)
                || authenticatedEntity.getId() == entity.getId();
    }

    /**
     * Check if the auth'ed entity can access an Entity owned resource.
     * This check applies to actions for updating the entity, sending confirmation emails, etc.
     * @param entity The entity to auth against
     * @return If the entity is allowed access
     */
    public boolean canAccess(Entity entity) {
        if(entity == null) {
            throw new IllegalArgumentException(
                    "Failed to authenticate permissions. " +
                    "Entity is null while checking canAccess(Entity)"
            );
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        return authenticatedEntity.isAdmin()
                || authenticatedEntity.getId() == entity.getId();
    }

    /**
     * Check if the auth'ed entity can send invites for a topic.
     * Only admins can send invites that are not in pending.
     * (e.g. normal users can only create topic members in 'pending')
     * TODO what if topic doesn't exist?
     * @param topic The resource to validate
     * @return Whether the user is allowed to create the topic member
     */
    public boolean canCreateTopicMember(Topic topic) {
        if(topic == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Topic is null.");
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        if(authenticatedEntity.isAdmin()) {
            return true;
        }

        TopicMemberStatus acceptedStatus = new TopicMemberStatusAccepted();
        TopicMember topicMember = this.topicMemberService.findByEntityAndTopicAndStatus(authenticatedEntity, topic, acceptedStatus);

        if (topicMember == null) {
            return false;
        }

        if (topicMember.getTopicRole().getId() == TopicRole.ADMIN) {
            return true;
        }

        // only topic members with the provided invite can send invites. Admins can always send invites.
        return isTopicMemberAllowed(
                topicMember.getTopic().getId(),
                topicMember.getTopicRole().getId(),
                TopicPermission.CAN_INVITE
        );
    }

    /**
     * Only members of the topic and admins can access associated topic content
     * @param topicContent The topic content to perform access validation against
     * @return whether or not the currently auth'ed entity can access this content
     */
    public boolean canView(TopicContent topicContent) {
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
    public boolean canView(Topic topic) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        TopicMember topicMember = this.topicMemberService.findByEntityAndTopic(authenticatedEntity, topic);

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
     * @param topicContentRequest TopicContentRequest to auth against
     * @return If the auth'ed user has access to the topic
     */
    public boolean canCreate(TopicContentRequest topicContentRequest) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();

        if (authenticatedEntity.isAdmin()) {
            return true;
        }

        Topic topic = new Topic();
        topic.setId(topicContentRequest.getTopicId());

        TopicMemberStatus acceptedStatus = new TopicMemberStatusAccepted();
        TopicMember topicMember = this.topicMemberService.findByEntityAndTopicAndStatus(authenticatedEntity, topic, acceptedStatus);

        if (topicMember == null) {
            return false;
        }

        if (topicMember.getTopicRole().getId() == TopicRole.ADMIN) {
            return true;
        }

        // only topic members with the correct permissions can post. Admins can always post.
        return isTopicMemberAllowed(
                topicMember.getTopic().getId(),
                topicMember.getTopicRole().getId(),
                TopicPermission.CAN_INVITE
        );
    }

    /**
     * Only topic creators can update topic details for now
     * TODO add moderator check
     * @param topic The topic to auth against
     * @return If the auth'ed user can modify the topic
     */
    public boolean canModify(Topic topic) {
        if (topic == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Topic is null.");
        }

        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        Topic currentTopic = topicService.findTopicById(topic.getId());


        if (currentTopic == null) {
            throw new ResourceNotFoundException("No topic with id " + topic.getId() + " exists!");
        }

        if (!isTopicMember(authenticatedEntity, currentTopic)) {
            return false;
        }

        if (topic.getOwner() != null) { // If they're updating the owner
            if (topic.getOwner().getId() != currentTopic.getOwner().getId()) { // if the owner has changed
                // only the current owner can transfer ownership
                if (currentTopic.getOwner().getId() != authenticatedEntity.getId()) {
                    return false;
                }
            }
        }

        // TODO check if they're a topic admin

        return authenticatedEntity.isAdmin() || authenticatedEntity.getId() == currentTopic.getCreator().getId();
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
     * the topic member status and topic role.
     *
     * Valid transitions for the topic member status
     * (pending|accepted) -> rescinded               by the inviter
     * rescinded          -> pending                 by the inviter
     * pending            -> (accepted | rejected)   by the invitee
     * accepted           -> left_topic              by the invitee
     * @param newTopicMember The new topic member object with the applied updates
     * @return If the auth'ed user can modify the topic member
     */
    public boolean canModify(TopicMember newTopicMember) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        TopicMember oldTopicMember = this.topicMemberService.findById(newTopicMember.getId());
        if(authenticatedEntity.isAdmin()) {
            return true;
        }

        if(newTopicMember.getStatus() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null topic member status.");
        }

        if(newTopicMember.getStatus() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null topic member status.");
        }

        if(newTopicMember.getCreator() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null creator.");
        }

        if(newTopicMember.getEntity() == null) {
            throw new BusinessLogicException("Failed to authenticate permissions. Null topic member entity.");
        }

        /*
         * Validate update to topic role
         * If the topicRole has changed and the current entity isn't the owner, fail.
         */
        if (newTopicMember.getTopicRole().getId() != oldTopicMember.getTopicRole().getId()
            && oldTopicMember.getTopic().getOwner().getId() != authenticatedEntity.getId()) {
            return false;
        }

        /*
         * Validate update the new topic member status
         */
        if(oldTopicMember.getStatus().getId() == newTopicMember.getStatus().getId()) {
            // If there was no change to the topic member status, then we're all good
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TopicMemberStatus.PENDING
                && newTopicMember.getStatus().getId()  == TopicMemberStatus.RESCINDED
                && oldTopicMember.getCreator().getId() == authenticatedEntity.getId()) {

            // Validate pending -> rescinded transition
            //    you sent the invite, but want to take it back
            //    auth'ed entity must be creator
            return true;
        } else if(oldTopicMember.getStatus().getId()       == TopicMemberStatus.ACCEPTED
                    && newTopicMember.getStatus().getId()  == TopicMemberStatus.RESCINDED
                    && oldTopicMember.getCreator().getId() == authenticatedEntity.getId()) {

            // Validate pending -> rescinded transition
            //    someone accepted an invite, but you want to remove them
            //    auth'ed entity must be creator
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TopicMemberStatus.PENDING
                && newTopicMember.getStatus().getId()  == TopicMemberStatus.ACCEPTED
                && oldTopicMember.getEntity().getId()  == authenticatedEntity.getId()) {
            // Validate pending -> accepted transition
            //    you send an invite, and someone accepts
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TopicMemberStatus.PENDING
                && newTopicMember.getStatus().getId()  == TopicMemberStatus.REJECTED
                && oldTopicMember.getEntity().getId()  == authenticatedEntity.getId()) {
            // Validate pending -> rejected transition
            //    you send an invite, and they reject it
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TopicMemberStatus.ACCEPTED
                && newTopicMember.getStatus().getId()  == TopicMemberStatus.LEFT_TOPIC
                && oldTopicMember.getEntity().getId()  == authenticatedEntity.getId()) {
            // Validate accepted -> left transition
            //    someone accepts an invite but later leaves the group
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TopicMemberStatus.RESCINDED
                && newTopicMember.getStatus().getId()  == TopicMemberStatus.PENDING
                && oldTopicMember.getCreator().getId()  == authenticatedEntity.getId()) {
            // Validate rescinded -> pending left transition
            //    you rescind an invite, but then change your mind again
            //    auth'ed entity must be future member
            return true;
        } else if(oldTopicMember.getStatus().getId()   == TopicMemberStatus.REJECTED
                && newTopicMember.getStatus().getId()  == TopicMemberStatus.ACCEPTED
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

    /**
     * Check whether the authenticated entity can update the given topicRolePermission
     * Only site and topic admins can update a topic's permissions settings
     * @param topicRolePermission
     * @return If the auth'ed user can modify the topic role permission
     */
    public boolean canModify(TopicRolePermission topicRolePermission) {
        Entity authenticatedEntity = this.entityService.getAuthenticatedEntity();
        if (authenticatedEntity.isAdmin()) {
            return authenticatedEntity.isAdmin();
        }

        // if the user is an admin they can update the permission's setting
        TopicMember topicMember = topicMemberService.findByEntityAndTopic(authenticatedEntity, topicRolePermission.getTopic());

        if (topicMember == null) {
            return false;
        }

        return topicMember.getTopicRole().getId() == TopicRole.ADMIN;

    }
}
