package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.TopicRolePermission;
import saturday.repositories.TopicRolePermissionRepository;

import java.util.List;

@Service()
public class TopicRolePermissionService {
    private final EntityService entityService;
    private final TopicRolePermissionRepository topicRolePermissionRepository;

    @Autowired
    public TopicRolePermissionService(
            EntityService entityService,
            TopicRolePermissionRepository topicRolePermissionRepository
    ) {
        this.entityService = entityService;
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

        TopicRolePermission existingTopicRolePermission = topicRolePermissionRepository.findByTopicIdAndTopicRoleIdAndTopicPermissionId(
                topicRolePermission.getTopic().getId(),
                topicRolePermission.getTopicRole().getId(),
                topicRolePermission.getTopicPermission().getId()
        );

        // TODO improve
        if (existingTopicRolePermission != null) {
            topicRolePermission.setId(
                    existingTopicRolePermission.getId()
            );
        }


        // The default value for the creator of a topic member is the current user
        // unless an admin set a creator
        Entity currentEntity = entityService.getAuthenticatedEntity();
        if (currentEntity.isAdmin() && topicRolePermission.getCreator() != null) {
            topicRolePermission.setCreator(topicRolePermission.getCreator());
        } else {
            topicRolePermission.setCreator(currentEntity);
        }

        // The default value for the creator of a topic member is the current user
        // unless an admin set a creator
        if (currentEntity.isAdmin() && topicRolePermission.getModifier() != null) {
            topicRolePermission.setModifier(topicRolePermission.getModifier());
        } else {
            topicRolePermission.setModifier(currentEntity);
        }

        this.topicRolePermissionRepository.save(topicRolePermission);
        return topicRolePermission;
    }

    public List<TopicRolePermission> findByEntityId(int entityId) {
        return this.topicRolePermissionRepository.findByEntityId(entityId);
    }

    public List<TopicRolePermission> findByTopicIdAndIsAllowed(int id, boolean isAllowed) {
        return this.topicRolePermissionRepository.findByTopicIdAndIsAllowed(id, isAllowed);
    }

    public TopicRolePermission findByTopicIdAndTopicRoleIdAndTopicPermissionId(
            int topicId,
            int topicRoleId,
            int topicPermissionId
    ) {
        return this.topicRolePermissionRepository.findByTopicIdAndTopicRoleIdAndTopicPermissionId(
                topicId,
                topicRoleId,
                topicPermissionId
        );
    }
}
