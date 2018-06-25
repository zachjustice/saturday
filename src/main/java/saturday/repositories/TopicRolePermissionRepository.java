package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import saturday.domain.Topic;
import saturday.domain.topicPermissions.TopicPermission;
import saturday.domain.topicRoles.TopicRole;
import saturday.domain.TopicRolePermission;

import java.util.List;

public interface TopicRolePermissionRepository extends JpaRepository<TopicRolePermission, Integer> {
    TopicRolePermission findById(int id);
    TopicRolePermission findByTopicAndTopicRoleAndTopicPermission(Topic topic, TopicRole topicRole, TopicPermission topicPermission);

    List<TopicRolePermission> findByTopicId(int topicId);

    List<TopicRolePermission> findByTopicIdAndIsAllowed(int id, boolean isAllowed);
    TopicRolePermission findByTopicIdAndTopicRoleIdAndTopicPermissionId(int topicId, int topicRoleId, int topicPermissionId);
}

