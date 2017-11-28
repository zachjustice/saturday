package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.TopicContent;

import java.util.List;

@Repository
public interface TopicContentRepository extends JpaRepository<TopicContent, Long> {
    List<TopicContent> findByTopicId(int id);
    TopicContent findById(int id);

    @Query(
        value = "select * from topic_content tc where tc.topic_id in (select tm.topic_id from topic_members tm where tm.entity_id = :userId)",
        nativeQuery = true
    )
    List<TopicContent> findByTopicMember(@Param("userId")int userId);
}
