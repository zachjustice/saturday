package saturday.delegates;

import org.springframework.stereotype.Component;
import saturday.domain.Topic;
import saturday.domain.TopicRolePermission;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.PermissionService;
import saturday.services.TopicRolePermissionService;
import saturday.services.TopicService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TopicRolePermissionDelegate {
    private final TopicService topicService;
    private final TopicRolePermissionService topicRolePermissionService;
    private final PermissionService permissionService;

    public TopicRolePermissionDelegate(
            TopicService topicService,
            TopicRolePermissionService topicRolePermissionService,
            PermissionService permissionService
    ) {
        this.topicService = topicService;
        this.topicRolePermissionService = topicRolePermissionService;
        this.permissionService = permissionService;
    }

    /**
     * Save a topic permission
     * @param topicRolePermission The new or existing topic permissions
     * @return The saved topic permission
     */
    public TopicRolePermission save(TopicRolePermission topicRolePermission) {
        if (!permissionService.canModify(topicRolePermission)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return topicRolePermissionService.save(topicRolePermission);
    }

    /**
     * Returns a map of allowed permissions for a topic id
     * Returned map has the form {
     *     'USER': [CAN_POST, ...],
     *     'MODERATOR': [CAN_POST, ...],
     *     'ADMIN': [CAN_POST, ...]
     * }
     * @param id Topic Id
     * @return Allowed permissions for the topic id
     */
    public Map<String, List<TopicRolePermission>> getPermissions(int id) {
        Topic topic = topicService.findTopicById(id);
        if (topic == null) {
            throw new ResourceNotFoundException("No topic with id " + id + " exists!");
        }

        // If you can view the topic, you can view the permissions
        if (!permissionService.canView(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        List<TopicRolePermission> allowedTopicRolePermissions = topicRolePermissionService.findByTopicIdAndIsAllowed(
                id,
                true
        );

        return allowedTopicRolePermissions
                .stream()
                .collect(
                        Collectors.groupingBy(
                                topicRolePermission -> topicRolePermission.getTopicRole().getRole()
                        )
                );
    }
}
