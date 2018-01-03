package saturday.services;

import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;

import java.io.IOException;
import java.util.List;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    List<TopicContent> findTopicContentByTopicId(int id);

    TopicContent save(TopicContentRequest topicContentRequest) throws IOException;
    TopicContent updateTopicContent(TopicContent oldTopicContent, TopicContent newTopicContent);
    List<TopicContent> findByTopicMember(int id, int offset, int limit);
    void delete(TopicContent topicContent);
}
