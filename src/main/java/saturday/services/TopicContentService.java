package saturday.services;

import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.exceptions.ProcessingResourceException;

import java.io.IOException;
import java.util.List;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    List<TopicContent> findTopicContentByTopicId(int id);

    TopicContent save(TopicContentRequest topicContentRequest) throws IOException, ProcessingResourceException;
    TopicContent updateTopicContent(TopicContent oldTopicContent, TopicContent newTopicContent);
    List<TopicContent> findByTopicMember(int entityId);
    void delete(TopicContent topicContent);
}
