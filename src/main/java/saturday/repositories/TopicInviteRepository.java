package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.domain.TopicInvite;

import java.util.List;

@Repository
public interface TopicInviteRepository extends JpaRepository<TopicInvite, Integer> {
    List<TopicInvite> findByTopicId(int id);
    List<TopicInvite> findTopicInvitesByInvitee(Entity invitee);
    List<TopicInvite> findTopicInvitesByInviter(Entity inviter);
    TopicInvite findTopicInviteByInviteeAndTopic(Entity invitee, Topic topic);
    TopicInvite findById(int id);
}
