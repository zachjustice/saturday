package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.exceptions.ProcessingResourceException;
import saturday.repositories.TopicMemberRepository;

import java.util.List;

@Service("topicMemberService")
public class TopicMemberServiceImpl implements TopicMemberService {
    private final TopicMemberRepository topicMemberRepository;
    private final EntityService entityService;
    private final TopicService topicService;
    private final TopicInviteService topicInviteService;

    @Autowired
    TopicMemberServiceImpl(TopicMemberRepository topicMemberRepository, EntityService entityService, TopicService topicService, TopicInviteService topicInviteService) {
        this.topicMemberRepository = topicMemberRepository;
        this.entityService = entityService;
        this.topicService = topicService;
        this.topicInviteService = topicInviteService;
    }

    @Override
    public TopicMember save(TopicMember topicMember) throws ProcessingResourceException {
        // check if the invitee is already a topic member
        TopicMember existingTopicMember = topicMemberRepository.findByEntityAndTopic(topicMember.getEntity(), topicMember.getTopic());
        if (existingTopicMember != null) {
            throw new ProcessingResourceException("Entity is already a member of this topic.");
        }

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
