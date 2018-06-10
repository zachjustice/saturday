package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import saturday.domain.Topic;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.TopicRepository;

import java.util.List;

@Service("topicService")
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final EntityService entityService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${saturday.topic.name.max_length}")
    private int TOPIC_NAME_MAX_LENGTH;
    @Value("${saturday.topic.description.max_length}")
    private int TOPIC_DESCRIPTION_MAX_LENGTH;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository, EntityService entityService) {
        this.topicRepository = topicRepository;
        this.entityService = entityService;
    }

    /**
     * Find topics with a matching name
     * @param name The name to search by
     * @return List of topics
     */
    @Override
    public List<Topic> findTopicByName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new BusinessLogicException("Search by topic name with an empty string.");
        }

        List<Topic> topics = topicRepository.findByName(name);

        if(topics == null) {
            throw new ResourceNotFoundException("No topic with name " + name + " exists!");
        }

        return topics;
    }

    @Override
    public Topic findTopicById(int id) {
        Topic topic = topicRepository.findById(id);

        if(topic == null) {
            throw new ResourceNotFoundException("No topic with id " + id + " exists!");
        }

        return topic;
    }

    @Override
    public Topic update(Topic topic) {

        if(StringUtils.isEmpty(topic.getName()) || topic.getName().length() > TOPIC_NAME_MAX_LENGTH) {
            throw new BusinessLogicException("Failed to update topic. Topic name must exist and be less than " + TOPIC_NAME_MAX_LENGTH + " characters.");
        }

        if(topic.getDescription() != null && topic.getDescription().length() > TOPIC_DESCRIPTION_MAX_LENGTH) {
            throw new BusinessLogicException("Failed to update topic. Topic description must be less than " + TOPIC_DESCRIPTION_MAX_LENGTH + " characters.");
        }

        if (topic.getOwner() == null) {
            throw new BusinessLogicException("Failed to update topic. Owner is null.");
        }

        return topicRepository.save(topic);
    }

    @Override
    public Topic save(Topic topic) {

        if(StringUtils.isEmpty(topic.getName()) || topic.getName().length() > TOPIC_NAME_MAX_LENGTH) {
            throw new BusinessLogicException("Invalid topic name. Topic name must exist and be less than " + TOPIC_NAME_MAX_LENGTH + " characters.");
        }

        if(topic.getDescription() != null && topic.getDescription().length() > TOPIC_DESCRIPTION_MAX_LENGTH) {
            throw new BusinessLogicException("Invalid topic description. Topic description must be less than " + TOPIC_DESCRIPTION_MAX_LENGTH + " characters.");
        }

        topic.setCreator(entityService.getAuthenticatedEntity());
        topic.setOwner(entityService.getAuthenticatedEntity());
        return topicRepository.save(topic);
    }

    @Override
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }
}


