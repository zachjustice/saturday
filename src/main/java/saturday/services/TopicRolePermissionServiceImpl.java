package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.TopicRolePermission;
import saturday.repositories.TopicRolePermissionRepository;

@Service("topicRolePermissionServiceImpl ")
public class TopicRolePermissionServiceImpl implements TopicRolePermissionService {
    private final TopicRolePermissionRepository topicRolePermissionRepository;

    @Autowired
    public TopicRolePermissionServiceImpl(TopicRolePermissionRepository topicRolePermissionRepository) {
        this.topicRolePermissionRepository = topicRolePermissionRepository;
    }

    public TopicRolePermission save(TopicRolePermission topicRolePermission) {

        if (topicRolePermission.getTopic() == null) {
            throw new IllegalArgumentException("Failed to save topicRolePermission. Null topic.");
        }

        if (topicRolePermission.getTopicRole() == null) {
            throw new IllegalArgumentException("Failed to save topicRolePermission. Null topicRole.");
        }

        if (topicRolePermission.getTopicPermission() == null) {
            throw new IllegalArgumentException("Failed to save topicRolePermission. Null topicRolePermission.");
        }

        if (topicRolePermission.isAllowed() == null) {
            throw new IllegalArgumentException("Failed to save topicRolePermission. Null isAllowed.");
        }

        this.topicRolePermissionRepository.save(topicRolePermission);
        return topicRolePermission;
    }
}
