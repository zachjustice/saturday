package saturday.delegates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.CreateTopicRequest;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.topicPermissions.TopicPermission;
import saturday.domain.topicPermissions.TopicPermissionCanInvite;
import saturday.domain.topicPermissions.TopicPermissionCanPost;
import saturday.domain.TopicRolePermission;
import saturday.domain.topicMemberStatuses.TopicMemberStatus;
import saturday.domain.topicMemberStatuses.TopicMemberStatusAccepted;
import saturday.domain.topicRoles.TopicAdmin;
import saturday.domain.topicRoles.TopicUser;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicMemberService;
import saturday.services.TopicRolePermissionService;
import saturday.services.TopicService;

import java.util.Arrays;
import java.util.List;

@Component()
public class TopicDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TopicService topicService;
    private final TopicMemberService topicMemberService;
    private final EntityService entityService;
    private final TopicRolePermissionService topicRolePermissionService;
    private final PermissionService permissionService;
    private final TopicMemberDelegate topicMemberDelegate;

    @Autowired
    public TopicDelegate(
            TopicService topicService,
            TopicMemberService topicMemberService,
            EntityService entityService,
            TopicRolePermissionService topicRolePermissionService,
            PermissionService permissionService,
            TopicMemberDelegate topicMemberDelegate) {
        this.topicService = topicService;
        this.topicMemberService = topicMemberService;
        this.entityService = entityService;
        this.topicRolePermissionService = topicRolePermissionService;
        this.permissionService = permissionService;
        this.topicMemberDelegate = topicMemberDelegate;
    }

    public Topic update(Topic topic) {
        if(!permissionService.canModify(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions");
        }

        return topicService.update(topic);
    }

    public Topic save(CreateTopicRequest createTopicRequest) {
        // TODO check 3 topics per minute

        // Create Topic
        Topic topic = topicService.save(createTopicRequest.getTopic());

        // Add creator of the topic as the only topic member with a role of admin
        setTopicOwner(topic);
        setTopicRolePermissions(topic);
        inviteInitialTopicMembers(createTopicRequest, topic);

        return createTopicRequest.getTopic();
    }

    private void inviteInitialTopicMembers(CreateTopicRequest createTopicRequest, Topic topic) {
        logger.info(createTopicRequest.getInitialTopicMemberEmails().toString());
        createTopicRequest
                .getInitialTopicMemberEmails()
                .forEach((email) -> {
                    try {
                        topicMemberDelegate.inviteByEmail(email, topic.getId());
                    } catch(ResourceNotFoundException ignored) { }
                });
    }

    private void setTopicOwner(Topic topic) {
        Entity currentEntity = entityService.getAuthenticatedEntity();

        TopicMemberStatus acceptedStatus = new TopicMemberStatusAccepted();
        TopicAdmin adminTopicRole = new TopicAdmin();

        TopicMember topicMember = new TopicMember();
        topicMember.setTopic(topic);
        topicMember.setCreator(currentEntity);
        topicMember.setEntity(currentEntity);
        topicMember.setTopicRole(adminTopicRole);

        topicMember.setStatus(acceptedStatus);
        topicMemberService.save(topicMember);
    }

    private void setTopicRolePermissions(Topic topic) {
        // Add default topic permissions for topic users
        //   users can post and invite by default
        TopicPermission[] allPermissions = new TopicPermission[]{
                new TopicPermissionCanPost(),
                new TopicPermissionCanInvite()
        };

        for(TopicPermission topicPermission: allPermissions) {
            TopicUser userTopicRole = new TopicUser();

            TopicRolePermission adminPermission = new TopicRolePermission();
            adminPermission.setTopic(topic);
            adminPermission.setTopicRole(userTopicRole);
            adminPermission.setTopicPermission(topicPermission);
            adminPermission.setIsAllowed(true);

            topicRolePermissionService.save(adminPermission);
        }
    }

    public List<Topic> getEntityTopics(int entityId) {
        Entity entity = entityService.findEntityById(entityId);

        if(!permissionService.canAccess(entity)) {
            throw new AccessDeniedException();
        }

        return this.topicService.findByEntityIdAndTopicMemberStatusId(entityId, TopicMemberStatus.ACCEPTED);
    }
}
