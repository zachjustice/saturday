package saturday.domain.topicMemberStatuses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import saturday.domain.Topic;
import saturday.domain.TopicMemberStatus;
import saturday.domain.topicRoles.TopicRole;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "topic_members")
public class TopicMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name="entity_id", referencedColumnName = "id", nullable=false, updatable = false)
    private saturday.domain.Entity entity;

    @ManyToOne
    @JoinColumn(name="topic_id", referencedColumnName = "id", nullable=false, updatable = false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name="status_id", referencedColumnName = "id", nullable = false)
    private TopicMemberStatus status;

    @ManyToOne
    @JoinColumn(name="topic_role_id", referencedColumnName = "id", nullable = false)
    private TopicRole topicRole;

    @ManyToOne
    @JoinColumn(name="creator_id", referencedColumnName = "id", nullable=false, updatable = false)
    private saturday.domain.Entity creator;

    @ManyToOne
    @JoinColumn(name="modifier_id", referencedColumnName = "id", nullable=false)
    private saturday.domain.Entity modifier;

    @Column(name = "created", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    @Column(name = "modified", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public saturday.domain.Entity getEntity() {
        return entity;
    }

    public void setEntity(saturday.domain.Entity entity) {
        this.entity = entity;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public TopicMemberStatus getStatus() {
        return status;
    }

    public void setStatus(TopicMemberStatus status) {
        this.status = status;
    }

    public saturday.domain.Entity getCreator() {
        return creator;
    }

    public void setCreator(saturday.domain.Entity creator) {
        this.creator = creator;
    }

    public saturday.domain.Entity getModifier() {
        return modifier;
    }

    public void setModifier(saturday.domain.Entity modifier) {
        this.modifier = modifier;
    }

    public TopicRole getTopicRole() {
        return topicRole;
    }

    public void setTopicRole(TopicRole topicRole) {
        this.topicRole = topicRole;
    }

    @Override
    public String toString() {
        return "TopicMember{" +
                "id=" + id +
                ", entity=" + entity +
                ", topic=" + topic +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicMember that = (TopicMember) o;
        return id == that.id &&
                Objects.equals(entity, that.entity) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(status, that.status) &&
                Objects.equals(topicRole, that.topicRole) &&
                Objects.equals(creator, that.creator) &&
                Objects.equals(modifier, that.modifier) &&
                Objects.equals(created, that.created) &&
                Objects.equals(modified, that.modified);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, entity, topic, status, topicRole, creator, modifier, created, modified);
    }
}
