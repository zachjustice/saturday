package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicMemberStatus;

import java.util.List;

@Repository
public interface TopicMemberRepository extends JpaRepository<TopicMember, Integer> {
    TopicMember findById(int id);
    TopicMember findByEntityAndTopic(Entity entity, Topic topic);
    TopicMember findByEntityAndTopicAndStatus(Entity entity, Topic topic, TopicMemberStatus status);

    List<TopicMember> findByTopicId(int id);

    @Query(
        value = "select id, entity_id, status_id, topic_id, creator_id, modifier_id, created, modified from topic_members where (creator_id = :creator_id or entity_id = :entity_id) and status_id = :status_id",
        nativeQuery = true
    )
    List<TopicMember> findAllByCreatorOrEntityAndStatus(
            @Param("creator_id") int creatorId,
            @Param("entity_id") int entityId,
            @Param("status_id") int statusId
    );
}
