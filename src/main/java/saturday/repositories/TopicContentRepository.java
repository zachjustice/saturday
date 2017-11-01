package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.TopicContent;

import java.util.List;

@Repository
public interface TopicContentRepository extends JpaRepository<TopicContent, Long> {
    List<TopicContent> findByTopicId(int id);
    TopicContent findById(int id);
}
