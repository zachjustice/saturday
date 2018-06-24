package saturday.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.topicMemberStatuses.TopicMemberStatusAccepted;
import saturday.domain.topicRoles.TopicAdmin;
import saturday.domain.TopicMember;
import saturday.domain.topicMemberStatuses.TopicMemberStatus;
import saturday.domain.TopicPermission;
import saturday.domain.TopicRolePermission;
import saturday.domain.topicRoles.TopicUser;
import saturday.exceptions.AccessDeniedException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicMemberService;
import saturday.services.TopicRolePermissionService;
import saturday.services.TopicService;

import java.util.List;

@Component()
public class TopicDelegate {

    private final TopicService topicService;
    private final TopicMemberService topicMemberService;
    private final EntityService entityService;
    private final TopicRolePermissionService topicRolePermissionService;
    private final PermissionService permissionService;

    @Value("${saturday.topic.permission.can_post}")
    private int TOPIC_PERMISSION_CAN_POST;
    @Value("${saturday.topic.permission.can_invite}")
    private int TOPIC_PERMISSION_CAN_INVITE;

    @Autowired
    public TopicDelegate(
            TopicService topicService,
            TopicMemberService topicMemberService,
            EntityService entityService,
            TopicRolePermissionService topicRolePermissionService,
            PermissionService permissionService
    ) {
        this.topicService = topicService;
        this.topicMemberService = topicMemberService;
        this.entityService = entityService;
        this.topicRolePermissionService = topicRolePermissionService;
        this.permissionService = permissionService;
    }

    public Topic update(Topic topic) {

        if(!permissionService.canModify(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions");
        }

        return topicService.update(topic);
    }

    public Topic save(Topic topic) {

        // Create Topic
        topic = topicService.save(topic);

        // Add creator of the topic as the only topic member with a role of admin
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

        // Add default topic permissions for topic users
        //   users can post and invite by default
        int[] allPermissions = new int[]{
                TOPIC_PERMISSION_CAN_POST,
                TOPIC_PERMISSION_CAN_INVITE
        };

        for(int topicPermissionId: allPermissions) {
            TopicUser userTopicRole = new TopicUser();

            TopicPermission topicPermission = new TopicPermission();
            topicPermission.setId(topicPermissionId);

            TopicRolePermission adminPermission = new TopicRolePermission();
            adminPermission.setTopic(topic);
            adminPermission.setTopicRole(userTopicRole);
            adminPermission.setTopicPermission(topicPermission);
            adminPermission.setIsAllowed(true);

            topicRolePermissionService.save(adminPermission);
        }

        return topic;
    }

    public List<Topic> getEntityTopics(int entityId) {
        Entity entity = entityService.findEntityById(entityId);

        if(!permissionService.canAccess(entity)) {
            throw new AccessDeniedException();
        }

        return this.topicService.findByEntityIdAndTopicMemberStatusId(entityId, TopicMemberStatus.ACCEPTED);
    }
}
