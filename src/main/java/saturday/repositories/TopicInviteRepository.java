package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicInvite;

import java.util.List;

@Repository
public interface TopicInviteRepository extends JpaRepository<TopicInvite, Integer> {
    List<TopicInvite> findByTopicId(int id);
    List<TopicInvite> findTopicInvitesByInvitee(Entity invitee);
    List<TopicInvite> findTopicInvitesByInviter(Entity inviter);
    TopicInvite findTopicInviteByInviteeAndTopic(Entity invitee, Topic topic);
    TopicInvite findById(int id);

    @Query(
            value = "select * from topic_invites where invitee_id = :userId or inviter_id = :userId",
            nativeQuery = true
    )
    List<TopicInvite> findTopicInvitesByInviteeOrInviter(@Param("userId") int userId);
}
