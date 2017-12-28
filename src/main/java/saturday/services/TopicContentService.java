package saturday.services;

import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.exceptions.BusinessLogicException;

import java.io.IOException;
import java.util.List;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    List<TopicContent> findTopicContentByTopicId(int id);

    TopicContent save(TopicContentRequest topicContentRequest) throws IOException, BusinessLogicException;
    TopicContent updateTopicContent(TopicContent oldTopicContent, TopicContent newTopicContent);
    List<TopicContent> findByTopicMember(int entityId);
    void delete(TopicContent topicContent);
}
