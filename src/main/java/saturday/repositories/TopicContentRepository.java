package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.TopicContent;

@Repository
public interface TopicContentRepository extends JpaRepository<TopicContent, Long> {
    TopicContent findByTopicId(int id);
    TopicContent findById(int id);
}
