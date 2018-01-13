package saturday.services;

import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicMemberStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicMemberService {
    TopicMember save(TopicMember topicMember) ;
    void delete(int id) ;

    TopicMember findById(int id) ;
    List<TopicMember> findByTopicId(int id);
    TopicMember findByEntityAndTopic(Entity entity, Topic topic);
    TopicMember findByEntityAndTopicAndStatus(Entity entity, Topic topic, TopicMemberStatus status);

    TopicMember update(TopicMember oldTopicMember, TopicMember newTopicMember) ;

    Map<String, List<TopicMember>> getSentAndReceivedTopicInvites(Entity involvedParty);
}
