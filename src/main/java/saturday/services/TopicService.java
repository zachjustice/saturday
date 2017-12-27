package saturday.services;

import saturday.domain.Topic;
import saturday.exceptions.ProcessingResourceException;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicService {
    List<Topic> findTopicByName(String name) throws ProcessingResourceException;
    Topic findTopicById(int id);

    Topic saveTopic(Topic topic) throws ProcessingResourceException;

    void delete(Topic topic);
}
