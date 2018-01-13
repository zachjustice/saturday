package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
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
    List<TopicMember> findAllByCreatorOrEntityAndStatus(Entity creator, Entity entity, TopicMemberStatus status);
}
