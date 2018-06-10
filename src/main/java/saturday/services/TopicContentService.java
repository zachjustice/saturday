package saturday.services;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.TopicContent;

import java.io.IOException;
import java.util.*;

public interface TopicContentService {
    TopicContent findTopicContentById(int id);

    Map<String, List<TopicContent>> findTopicContentByTopicIdGroupedByDateTaken(Date start, Date end, int topicId);

    Page<TopicContent> findTopicContentByTopicId(Pageable page, int topicId);

    List<TopicContent> findByTopicMember(int id, int offset, int limit);

    TopicContent update(TopicContent newTopicContent);

    TopicContent save(MultipartFile file, Integer topicId, Integer creatorId, String description, Date dateTaken) throws IOException;

    TopicContent save(String data, Integer topicId, Integer creatorId, String description) throws IOException;

    void delete(TopicContent topicContent);
}
