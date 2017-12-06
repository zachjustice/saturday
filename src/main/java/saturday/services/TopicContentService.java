package saturday.services;

import org.springframework.web.multipart.MultipartFile;
import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;

import java.io.IOException;
import java.util.List;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    List<TopicContent> findTopicContentByTopicId(int id);
    TopicContent save(MultipartFile file, int creatorId, int topicId, String description) throws IOException;
    TopicContent updateTopicContent(TopicContent oldTopicContent, TopicContent newTopicContent);
    List<TopicContent> findByTopicMember(int entityId);
    void delete(TopicContent topicContent);
}
