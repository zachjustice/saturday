package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.TopicContent;

import java.util.List;

@Repository
public interface TopicContentRepository extends JpaRepository<TopicContent, Integer> {
    List<TopicContent> findByTopicId(int id);
    TopicContent findById(int id);
    void delete(int id);

    @Query(
        value = "select * from topic_content tc where tc.topic_id in (select tm.topic_id from topic_members tm where tm.entity_id = :entityId) OFFSET :offset LIMIT :limit",
        nativeQuery = true
    )
    List<TopicContent> findByTopicMember(
            @Param("entityId")int entityId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
}
