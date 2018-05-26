package saturday.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.TopicContent;

import java.util.Date;
import java.util.List;

@Repository
public interface TopicContentRepository extends PagingAndSortingRepository<TopicContent, Integer> {
    TopicContent findById(int id);
    void deleteById(int id);

    Page<TopicContent> findAllByTopicId(Pageable page, int topicId);
    List<TopicContent> findTopicContentByTopicIdAndDateTakenBetweenOrderByDateTakenDesc(int topicId, Date start, Date end);

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
