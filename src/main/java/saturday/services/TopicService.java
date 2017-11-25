package saturday.services;

import saturday.domain.Topic;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicService {
    Topic findTopicByName(String name);
    Topic findTopicById(int id);

    Topic saveTopic(Topic topic);
}
