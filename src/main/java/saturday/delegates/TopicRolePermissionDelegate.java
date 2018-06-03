package saturday.delegates;

import org.springframework.stereotype.Component;
import saturday.domain.TopicRolePermission;
import saturday.exceptions.AccessDeniedException;
import saturday.services.PermissionService;
import saturday.services.TopicRolePermissionService;

@Component
public class TopicRolePermissionDelegate {
    private final TopicRolePermissionService topicRolePermissionService;
    private final PermissionService permissionService;

    public TopicRolePermissionDelegate(TopicRolePermissionService topicRolePermissionService, PermissionService permissionService) {
        this.topicRolePermissionService = topicRolePermissionService;
        this.permissionService = permissionService;
    }

    public TopicRolePermission save(TopicRolePermission topicRolePermission) {
        if (!permissionService.canModify(topicRolePermission)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return topicRolePermissionService.save(topicRolePermission);
    }
}
