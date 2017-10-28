package saturday.services;

import saturday.domain.TopicContent;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    TopicContent findTopicContentByTopicId(int id);

    TopicContent saveTopicContent(TopicContent topic);
}
