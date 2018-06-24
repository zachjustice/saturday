package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicPermission;
import saturday.domain.TopicRolePermission;

import java.util.List;

public interface TopicRolePermissionRepository extends JpaRepository<TopicRolePermission, Integer> {
    TopicRolePermission findById(int id);

    TopicRolePermission findByTopicAndTopicRoleAndTopicPermission(Topic topic, TopicMember.TopicRole topicRole, TopicPermission topicPermission);

    List<TopicRolePermission> findByTopicId(int topicId);

    List<TopicRolePermission> findByTopicIdAndIsAllowed(int id, boolean isAllowed);

    TopicRolePermission findByTopicIdAndTopicRoleAndTopicPermission(int topicId, TopicMember.TopicRole topicRole, TopicPermission topicPermission);
}

