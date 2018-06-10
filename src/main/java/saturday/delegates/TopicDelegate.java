package saturday.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicMemberStatus;
import saturday.domain.TopicPermission;
import saturday.domain.TopicRole;
import saturday.domain.TopicRolePermission;
import saturday.services.EntityService;
import saturday.services.TopicMemberService;
import saturday.services.TopicRolePermissionService;
import saturday.services.TopicService;

@Component()
public class TopicDelegate {

    private final TopicService topicService;
    private final TopicMemberService topicMemberService;
    private final EntityService entityService;
    private final TopicRolePermissionService topicRolePermissionService;

    @Value("${saturday.topic.invite.status.accepted}")
    private int TOPIC_MEMBER_STATUS_ACCEPTED;

    @Value("${saturday.topic.role.user}")
    private int TOPIC_ROLE_USER;
    @Value("${saturday.topic.role.admin}")
    private int TOPIC_ROLE_ADMIN;

    @Value("${saturday.topic.permission.can_post}")
    private int TOPIC_PERMISSION_CAN_POST;
    @Value("${saturday.topic.permission.can_invite}")
    private int TOPIC_PERMISSION_CAN_INVITE;

    @Autowired
    public TopicDelegate(
            TopicService topicService,
            TopicMemberService topicMemberService,
            EntityService entityService,
            TopicRolePermissionService topicRolePermissionService
    ) {
        this.topicService = topicService;
        this.topicMemberService = topicMemberService;
        this.entityService = entityService;
        this.topicRolePermissionService = topicRolePermissionService;
    }

    public Topic save(Topic topic) {

        // Create Topic
        topic = topicService.save(topic);

        // Add creator of the topic as the only topic member with a role of admin
        Entity currentEntity = entityService.getAuthenticatedEntity();

        TopicMemberStatus acceptedStatus = new TopicMemberStatus();
        acceptedStatus.setId(TOPIC_MEMBER_STATUS_ACCEPTED);

        TopicRole adminTopicRole = new TopicRole();
        adminTopicRole.setId(TOPIC_ROLE_ADMIN);

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
            TopicRole userTopicRole = new TopicRole();
            userTopicRole.setId(TOPIC_ROLE_USER);

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
}
