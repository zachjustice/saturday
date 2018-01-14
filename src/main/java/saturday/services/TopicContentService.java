package saturday.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;

import java.io.IOException;
import java.util.List;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);
    Page<TopicContent> findTopicContentByTopicId(Pageable page, int topicId);

    TopicContent save(TopicContentRequest topicContentRequest) throws IOException;
    TopicContent updateTopicContent(TopicContent oldTopicContent, TopicContent newTopicContent);
    List<TopicContent> findByTopicMember(int id, int offset, int limit);
    void delete(TopicContent topicContent);
}
