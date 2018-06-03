package saturday.services;

import saturday.domain.TopicRolePermission;

import java.util.List;

public interface TopicRolePermissionService {
    TopicRolePermission save(TopicRolePermission topicRolePermission);

    List<TopicRolePermission> findByTopicId(int topicId);

    List<TopicRolePermission> findByTopicIdAndIsAllowed(int id, boolean isAllowed);
}
