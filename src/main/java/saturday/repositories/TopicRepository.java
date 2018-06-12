package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.Topic;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByName(String name);
    Topic findById(int id);

    @Query(
            value = "select * from topics where id in (select topic_id from topic_members where entity_id = :entity_id and status_id = :status_id)",
            nativeQuery = true
    )
    List<Topic> findByEntityIdAndTopicMemberStatusId(
            @Param("entity_id") int entityId,
            @Param("status_id") int statusId
    );
}
