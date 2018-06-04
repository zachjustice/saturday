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
    @Value("${saturday.topic.role.moderator}")
    private int TOPIC_ROLE_MODERATOR;
    @Value("${saturday.topic.role.admin}")
    private int TOPIC_ROLE_ADMIN;

    @Value("${saturday.topic.permission.can_post}")
    private int TOPIC_PERMISSION_CAN_POST;
    @Value("${saturday.topic.permission.can_delete_topic_content}")
    private int TOPIC_PERMISSION_CAN_DELETE_TOPIC_CONTENT;
    @Value("${saturday.topic.permission.can_invite}")
    private int TOPIC_PERMISSION_CAN_INVITE;
    @Value("${saturday.topic.permission.can_remove_members}")
    private int TOPIC_PERMISSION_CAN_REMOVE_MEMBERS;
    @Value("${saturday.topic.permission.can_cancel_invites}")
    private int TOPIC_PERMISSION_CAN_CANCEL_INVITES;
    @Value("${saturday.topic.permission.can_edit_group_info}")
    private int TOPIC_PERMISSION_CAN_EDIT_GROUP_INFO;
    @Value("${saturday.topic.permission.can_promote_users}")
    private int TOPIC_PERMISSION_CAN_PROMOTE_USERS;

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
        topic = topicService.saveTopic(topic);

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

        // Add default topic permissions
        // Users can post
        TopicRole userTopicRole = new TopicRole();
        userTopicRole.setId(TOPIC_ROLE_USER);

        TopicPermission canPostPermission = new TopicPermission();
        canPostPermission.setId(TOPIC_PERMISSION_CAN_POST);

        TopicRolePermission usersCanPost = new TopicRolePermission();
        usersCanPost.setTopic(topic);
        usersCanPost.setTopicRole(userTopicRole);
        usersCanPost.setTopicPermission(canPostPermission);
        usersCanPost.setIsAllowed(true);

        topicRolePermissionService.save(usersCanPost);

        // Moderators can post and delete posts
        TopicRole moderatorTopicRole = new TopicRole();
        moderatorTopicRole.setId(TOPIC_ROLE_MODERATOR);

        TopicRolePermission modsCanPost = new TopicRolePermission();
        modsCanPost.setTopic(topic);
        modsCanPost.setTopicRole(moderatorTopicRole);
        modsCanPost.setTopicPermission(canPostPermission);
        modsCanPost.setIsAllowed(true);

        topicRolePermissionService.save(modsCanPost);

        TopicPermission canDeletePermission = new TopicPermission();
        canDeletePermission.setId(TOPIC_PERMISSION_CAN_DELETE_TOPIC_CONTENT);

        TopicRolePermission modsCanDelete = new TopicRolePermission();
        modsCanDelete.setTopic(topic);
        modsCanDelete.setTopicRole(moderatorTopicRole);
        modsCanDelete.setTopicPermission(canDeletePermission);
        modsCanDelete.setIsAllowed(true);

        topicRolePermissionService.save(modsCanDelete);

        // Admins can do everything
        int[] allPermissions = new int[]{
                TOPIC_PERMISSION_CAN_POST,
                TOPIC_PERMISSION_CAN_DELETE_TOPIC_CONTENT,
                TOPIC_PERMISSION_CAN_INVITE,
                TOPIC_PERMISSION_CAN_REMOVE_MEMBERS,
                TOPIC_PERMISSION_CAN_CANCEL_INVITES,
                TOPIC_PERMISSION_CAN_EDIT_GROUP_INFO,
                TOPIC_PERMISSION_CAN_PROMOTE_USERS
        };

        for(int topicPermissionId: allPermissions) {
            TopicPermission topicPermission = new TopicPermission();
            topicPermission.setId(topicPermissionId);

            TopicRolePermission adminPermission = new TopicRolePermission();
            adminPermission.setTopic(topic);
            adminPermission.setTopicRole(adminTopicRole);
            adminPermission.setTopicPermission(topicPermission);
            adminPermission.setIsAllowed(true);

            topicRolePermissionService.save(adminPermission);
        }

        return topic;
    }
}
