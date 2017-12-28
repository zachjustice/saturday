package saturday.services;

import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;

import java.io.IOException;
import java.util.List;

public interface TopicContentService {
    TopicContent findTopicContentById(int id) throws ResourceNotFoundException;
    List<TopicContent> findTopicContentByTopicId(int id) throws ResourceNotFoundException;

    TopicContent save(TopicContentRequest topicContentRequest) throws IOException, BusinessLogicException, ResourceNotFoundException;
    TopicContent updateTopicContent(TopicContent oldTopicContent, TopicContent newTopicContent);
    List<TopicContent> findByTopicMember(int entityId) throws ResourceNotFoundException;
    void delete(TopicContent topicContent);
}
