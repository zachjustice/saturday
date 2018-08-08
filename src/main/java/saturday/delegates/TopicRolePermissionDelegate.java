package saturday.delegates;

import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicRolePermission;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicRolePermissionService;
import saturday.services.TopicService;
import saturday.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static saturday.utils.CommonUtils.getOrDefault;

@Component
public class TopicRolePermissionDelegate {
    private final TopicRolePermissionService topicRolePermissionService;
    private final PermissionService permissionService;
    private TopicService topicService;
    private EntityService entityService;

    public TopicRolePermissionDelegate(
            TopicRolePermissionService topicRolePermissionService,
            PermissionService permissionService,
            TopicService topicService,
            EntityService entityService
    ) {
        this.topicRolePermissionService = topicRolePermissionService;
        this.permissionService = permissionService;
        this.topicService = topicService;
        this.entityService = entityService;
    }

    /**
     * Save a topic permission
     * @param topicRolePermission The new or existing topic permissions
     * @return The saved topic permission
     */
    public TopicRolePermission save(TopicRolePermission topicRolePermission) {
        if (!permissionService.canModify(topicRolePermission)) {
            throw new AccessDeniedException();
        }

        return topicRolePermissionService.save(topicRolePermission);
    }

    /**
     * Returns a map of allowed permissions for a topic entityId
     * Returned map has the form {
     *     'USER': [CAN_POST, ...],
     *     'MODERATOR': [CAN_POST, ...],
     *     'ADMIN': [CAN_POST, ...]
     * }
     * @param entityId Entity Id
     * @return Allowed permissions for the topic entityId
     */
    public List<TopicRolePermission> getEntityPermissions(int entityId) {
        Entity entity = entityService.findEntityById(entityId);
        if (entity == null) {
            throw new ResourceNotFoundException("No entity with entityId " + entityId + " exists!");
        }

        // If you can view the entity, you can view the permissions
        if (!permissionService.canAccess(entity)) {
            throw new AccessDeniedException();
        }

        return topicRolePermissionService.findByEntityId(entityId);
    }

    public List<TopicRolePermission> findByTopicId(int topicId) {
        Topic topic = topicService.findTopicById(topicId);
        if (topic == null) {
            throw new ResourceNotFoundException("No entity with topic id " + topicId + " exists!");
        }

        // If you can view the topic, you can view the permissions
        if (!permissionService.canView(topic)) {
            throw new AccessDeniedException();
        }

        return getOrDefault(
                () -> topicRolePermissionService.findByTopicId(topicId),
                new ArrayList<>()
        );
    }
}
