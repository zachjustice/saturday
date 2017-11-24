package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicInvite;
import saturday.repositories.TopicInviteRepository;
import saturday.repositories.TopicRepository;

import java.util.List;

@Service("topicService")
public class TopicInviteServiceImpl implements TopicInviteService {
    private final TopicInviteRepository topicInviteRepository;

    @Autowired
    TopicInviteServiceImpl(TopicInviteRepository topicInviteRepository) {
        this.topicInviteRepository = topicInviteRepository;
    }

    @Override
    public List<TopicInvite> findByTopicId(int id) {
        return topicInviteRepository.findByTopicId(id);
    }

    @Override
    public List<TopicInvite> findTopicInvitesByInvitee(Entity invitee) {
        return topicInviteRepository.findTopicInvitesByInvitee(invitee);
    }

    @Override
    public List<TopicInvite> findTopicInvitesByInviter(Entity inviter) {
        return topicInviteRepository.findTopicInvitesByInviter(inviter);
    }

    @Override
    public TopicInvite findTopicInviteByInviteeAndTopic(Entity invitee, Topic topic) {
        return topicInviteRepository.findTopicInviteByInviteeAndTopic(invitee, topic);
    }

    @Override
    public TopicInvite save(TopicInvite topicInvite) {
        return topicInviteRepository.save(topicInvite);
    }

    @Override
    public void delete(int id) {
        topicInviteRepository.delete(id);
    }

    @Override
    public TopicInvite findById(int id) {
        return topicInviteRepository.findById(id);
    }
}
