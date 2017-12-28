package saturday.services;

import saturday.domain.Topic;
import saturday.exceptions.BusinessLogicException;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicService {
    List<Topic> findTopicByName(String name) throws BusinessLogicException;
    Topic findTopicById(int id);

    Topic saveTopic(Topic topic) throws BusinessLogicException;

    void delete(Topic topic);
}
