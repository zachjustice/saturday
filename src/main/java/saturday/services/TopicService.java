package saturday.services;

import saturday.domain.Topic;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicService {
    List<Topic> findTopicByName(String name) throws BusinessLogicException, ResourceNotFoundException;
    Topic findTopicById(int id) throws ResourceNotFoundException;

    Topic saveTopic(Topic topic) throws BusinessLogicException;

    void delete(Topic topic);
}
