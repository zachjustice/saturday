package saturday.services;

import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicMemberStatus;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicMemberService {
    TopicMember save(TopicMember topicMember) throws BusinessLogicException;
    void delete(int id);

    TopicMember findById(int id) throws ResourceNotFoundException;
    List<TopicMember> findByTopicId(int id);
    TopicMember findByEntityAndTopic(Entity entity, Topic topic);
    TopicMember findByEntityAndTopicAndStatus(Entity entity, Topic topic, TopicMemberStatus status);

    TopicMember update(TopicMember oldTopicMember, TopicMember newTopicMember) throws BusinessLogicException;
}
