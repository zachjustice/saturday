package saturday.services;

import saturday.domain.TopicContent;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    TopicContent findTopicContentByTopicId(int id);

    TopicContent saveTopic(TopicContent topic);
}
