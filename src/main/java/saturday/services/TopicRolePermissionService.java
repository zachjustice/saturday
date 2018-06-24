package saturday.services;

import saturday.domain.TopicMember;
import saturday.domain.TopicRolePermission;

import java.util.List;

public interface TopicRolePermissionService {
    TopicRolePermission save(TopicRolePermission topicRolePermission);

    List<TopicRolePermission> findByTopicId(int topicId);

    List<TopicRolePermission> findByTopicIdAndIsAllowed(int id, boolean isAllowed);

    TopicRolePermission findByTopicIdAndTopicRoleAndTopicPermissionId(int topicId, TopicMember.TopicRole topicRole, int topicPermissionId);
}
