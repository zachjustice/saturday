package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;

import java.util.List;

@Repository
public interface TopicMemberRepository extends JpaRepository<TopicMember, Integer> {
    List<TopicMember> findByTopicId(int id);
    TopicMember findById(int id);
    TopicMember findByEntityAndTopic(Entity entity, Topic topic);
}
