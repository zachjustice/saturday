package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saturday.domain.Topic;
import saturday.domain.topicPermissions.TopicPermission;
import saturday.domain.topicRoles.TopicRole;
import saturday.domain.TopicRolePermission;

import java.util.List;

public interface TopicRolePermissionRepository extends JpaRepository<TopicRolePermission, Integer> {
    TopicRolePermission findById(int id);
    TopicRolePermission findByTopicAndTopicRoleAndTopicPermission(Topic topic, TopicRole topicRole, TopicPermission topicPermission);

    @Query(
            value = "select trp.* from topic_role_permissions trp " +
                    "join topic_members tm " +
                    "   on tm.topic_id = trp.topic_id " +
                    "   and trp.topic_role_id = tm.topic_role_id  " +
                    "where tm.entity_id = :entity_id",
            nativeQuery = true
    )
    List<TopicRolePermission> findByEntityId(@Param("entity_id") int entityId);

    List<TopicRolePermission> findByTopicIdAndIsAllowed(int id, boolean isAllowed);
    TopicRolePermission findByTopicIdAndTopicRoleIdAndTopicPermissionId(int topicId, int topicRoleId, int topicPermissionId);
}
