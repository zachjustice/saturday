package saturday.services;

import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.exceptions.ProcessingResourceException;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicMemberService {
    TopicMember save(TopicMember topicMember) throws ProcessingResourceException;
    void delete(int id);

    TopicMember findById(int id);
    List<TopicMember> findByTopicId(int id);
    TopicMember findByEntityAndTopic(Entity entity, Topic topic);
}
