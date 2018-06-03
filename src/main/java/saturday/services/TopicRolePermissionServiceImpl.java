package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.TopicRolePermission;
import saturday.repositories.TopicRolePermissionRepository;

import java.util.List;

@Service("topicRolePermissionServiceImpl ")
public class TopicRolePermissionServiceImpl implements TopicRolePermissionService {
    private final EntityService entityService;
    private final TopicRolePermissionRepository topicRolePermissionRepository;

    @Autowired
    public TopicRolePermissionServiceImpl(
            EntityService entityService,
            TopicRolePermissionRepository topicRolePermissionRepository
    ) {
        this.entityService = entityService;
        this.topicRolePermissionRepository = topicRolePermissionRepository;
    }

    @Override
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

        // The default value for the creator of a topic member is the current user
        // unless an admin set a creator
        Entity currentEntity = entityService.getAuthenticatedEntity();
        if(currentEntity.isAdmin() && topicRolePermission.getCreator() != null) {
            topicRolePermission.setCreator(topicRolePermission.getCreator());
        } else {
            topicRolePermission.setCreator(currentEntity);
        }

        // The default value for the creator of a topic member is the current user
        // unless an admin set a creator
        if(currentEntity.isAdmin() && topicRolePermission.getModifier() != null) {
            topicRolePermission.setModifier(topicRolePermission.getModifier());
        } else {
            topicRolePermission.setModifier(currentEntity);
        }

        this.topicRolePermissionRepository.save(topicRolePermission);
        return topicRolePermission;
    }

    @Override
    public List<TopicRolePermission> findByTopicId(int topicId) {
        return this.topicRolePermissionRepository.findByTopicId(topicId);
    }

    @Override
    public List<TopicRolePermission> findByTopicIdAndIsAllowed(int id, boolean isAllowed) {
        return this.topicRolePermissionRepository.findByTopicIdAndIsAllowed(id, isAllowed);
    }
}
