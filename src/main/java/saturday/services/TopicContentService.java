package saturday.services;

import saturday.domain.TopicContent;

import java.util.List;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    List<TopicContent> findTopicContentByTopicId(int id);
    TopicContent saveTopicContent(TopicContent topic);
}
