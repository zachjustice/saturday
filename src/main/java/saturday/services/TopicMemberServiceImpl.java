package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicInvite;
import saturday.domain.TopicMember;
import saturday.repositories.TopicMemberRepository;

import javax.persistence.EntityNotFoundException;
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
    public TopicMember save(TopicMember topicMember) {
        return topicMemberRepository.save(topicMember);
    }

    @Override
    public TopicMember save(TopicInvite topicInvite) {
        TopicMember topicMember = new TopicMember();

        Entity member = entityService.findEntityById(topicInvite.getInvitee().getId());
        if(member == null) {
            throw new EntityNotFoundException("No entity with id " + topicInvite.getInvitee().getId());
        }

        Topic topic = topicService.findTopicById(topicInvite.getTopic().getId());
        if(topic == null) {
            throw new EntityNotFoundException("No topic with id " + topicInvite.getTopic().getId());
        }

        topicMember.setEntity(member);
        topicMember.setTopic(topic);

        // TODO catch unique constraint validatation
        topicMember = topicMemberRepository.save(topicMember);
        // TODO catch empty data access
        topicInviteService.delete(topicInvite.getId());
        return topicMember;
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
