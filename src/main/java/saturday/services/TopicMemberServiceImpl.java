package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.repositories.TopicMemberRepository;

import java.util.List;

@Service("topicMemberService")
public class TopicMemberServiceImpl implements TopicMemberService {
    private final TopicMemberRepository topicMemberRepository;

    @Autowired
    TopicMemberServiceImpl(TopicMemberRepository topicMemberRepository) {
        this.topicMemberRepository = topicMemberRepository;
    }

    @Override
    public TopicMember save(TopicMember topicMember) {
        return topicMemberRepository.save(topicMember);
    }

    @Override
    public void delete(int id) {
        topicMemberRepository.delete(id);
    }

    @Override
    public TopicMember findById(int id) {
        return topicMemberRepository.findById(id);
    }

    @Override
    public List<TopicMember> findByTopicId(int id) {
        return topicMemberRepository.findByTopicId(id);
    }

    @Override
    public TopicMember findByEntityAndTopic(Entity entity, Topic topic) {
        return topicMemberRepository.findByEntityAndTopic(entity, topic);
    }
}
