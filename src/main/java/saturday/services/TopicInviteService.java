package saturday.services;

import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicInvite;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicInviteService {
    TopicInvite save(TopicInvite topicInvite);
    void delete(int id);

    TopicInvite findById(int id);
    List<TopicInvite> findByTopicId(int id);
    List<TopicInvite> findTopicInvitesByInvitee(Entity invitee);
    List<TopicInvite> findTopicInvitesByInviter(Entity inviter);
    TopicInvite findTopicInviteByInviteeAndTopic(Entity invitee, Topic topic);
}
