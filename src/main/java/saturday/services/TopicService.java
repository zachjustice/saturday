package saturday.services;

import saturday.domain.Topic;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicService {
    List<Topic> findTopicByName(String name);
    Topic findTopicById(int id) ;

    Topic update(Topic topic);

    Topic save(Topic topic) ;

    void delete(Topic topic);
}
