package saturday.services;

import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicInvite;
import saturday.domain.TopicMember;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicMemberService {
    TopicMember save(TopicMember topicMember);
    TopicMember save(TopicInvite topicInvite);
    void delete(int id);

    TopicMember findById(int id);
    List<TopicMember> findByTopicId(int id);
    TopicMember findByEntityAndTopic(Entity entity, Topic topic);
}
