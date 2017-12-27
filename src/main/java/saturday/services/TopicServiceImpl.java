package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import saturday.domain.Topic;
import saturday.exceptions.ProcessingResourceException;
import saturday.repositories.TopicRepository;

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

    @Override
    public Topic findTopicByName(String name) throws ProcessingResourceException {
        if (StringUtils.isEmpty(name)) {
            throw new ProcessingResourceException("Search by topic name with an empty string.");
        }

        Topic topic = topicRepository.findByName(name);

        if(topic == null) {
            throw new ResourceNotFoundException("No topic with name " + name + " exists!");
        }

        return topic;
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
    public Topic saveTopic(Topic topic) throws ProcessingResourceException {

        if(StringUtils.isEmpty(topic.getName()) || topic.getName().length() > TOPIC_NAME_MAX_LENGTH) {
            throw new ProcessingResourceException("Invalid topic name. Topic name must exist and be less than " + TOPIC_NAME_MAX_LENGTH + " characters.");
        }

        if(topic.getDescription() != null && topic.getDescription().length() > TOPIC_DESCRIPTION_MAX_LENGTH) {
            throw new ProcessingResourceException("Invalid topic description. Topic description must be less than " + TOPIC_DESCRIPTION_MAX_LENGTH + " characters.");
        }

        topic.setCreator(entityService.getAuthenticatedEntity());
        return topicRepository.save(topic);
    }

    @Override
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }
}


