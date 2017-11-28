package saturday.services;

import org.springframework.stereotype.Service;
import saturday.domain.TopicContent;
import saturday.repositories.TopicContentRepository;

import java.util.List;

@Service("topicContentService")
public class TopicContentServiceImpl implements TopicContentService {
    private final TopicContentRepository topicContentRepository;

    public TopicContentServiceImpl(TopicContentRepository topicContentRepository) {
        this.topicContentRepository = topicContentRepository;
    }

    @Override
    public TopicContent findTopicContentById(int id) {
        return topicContentRepository.findById(id);
    }

    @Override
    public List<TopicContent> findTopicContentByTopicId(int id) {
        return topicContentRepository.findByTopicId(id);
    }

    @Override
    public TopicContent saveTopicContent(TopicContent topic) {
        // TODO validation
        return topicContentRepository.save(topic);
    }

    @Override
    public List<TopicContent> findByTopicMember(int userId) {
        return topicContentRepository.findByTopicMember(userId);
    }
}
