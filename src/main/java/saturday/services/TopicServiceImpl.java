package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Role;
import saturday.domain.Topic;
import saturday.repositories.EntityRepository;
import saturday.repositories.RoleRepository;
import saturday.repositories.TopicRepository;

import java.util.Collections;
import java.util.HashSet;

@Service("topicService")
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    public Topic findTopicByName(String name) {
        return topicRepository.findByName(name);
    }

    @Override
    public Topic findTopicById(int id) {
        return topicRepository.findById(id);
    }

    @Override
    public Topic saveTopic(Topic topic) {
        // TODO validate topic before saving
        return topicRepository.save(topic);
    }
}


