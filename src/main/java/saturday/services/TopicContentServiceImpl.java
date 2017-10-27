package saturday.services;

import org.springframework.stereotype.Service;
import saturday.domain.TopicContent;
import saturday.repositories.TopicContentRepository;
import saturday.repositories.TopicRepository;

@Service("topicContentService")
public class TopicContentServiceImpl implements TopicContentService {
    public final TopicContentRepository topicContentRepository;

    public TopicContentServiceImpl(TopicContentRepository topicContentRepository) {
        this.topicContentRepository = topicContentRepository;
    }

    @Override
    public TopicContent findTopicContentById(int id) {
        return topicContentRepository.findById(id);
    }

    @Override
    public TopicContent findTopicContentByTopicId(int id) {
        return topicContentRepository.findByTopicId(id);
    }

    @Override
    public TopicContent saveTopic(TopicContent topic) {
        // TODO validation
        return topicContentRepository.save(topic);
    }
}
