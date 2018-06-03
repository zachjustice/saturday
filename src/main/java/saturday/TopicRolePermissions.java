package saturday;

import saturday.domain.Topic;
import saturday.domain.TopicPermission;
import saturday.domain.TopicRole;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "topic_role_permissions")
public class TopicRolePermissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name="topic_id", referencedColumnName = "id", nullable=false, updatable = false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name="topic_role_id", referencedColumnName = "id", nullable=false, updatable = false)
    private TopicRole topicRole;

    @ManyToOne
    @JoinColumn(name="topic_permission_id", referencedColumnName = "id", nullable=false, updatable = false)
    private TopicPermission topicPermission;

    @Column(name = "is_allowed", nullable = false)
    private Boolean isAllowed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public TopicRole getTopicRole() {
        return topicRole;
    }

    public void setTopicRole(TopicRole topicRole) {
        this.topicRole = topicRole;
    }

    public TopicPermission getTopicPermission() {
        return topicPermission;
    }

    public void setTopicPermission(TopicPermission topicPermission) {
        this.topicPermission = topicPermission;
    }

    public Boolean isAllowed() {
        return isAllowed;
    }

    public void setIsAllowed(Boolean allowed) {
        isAllowed = allowed;
    }

    @Override
    public String toString() {
        return "TopicRolePermissions{" +
                "id=" + id +
                ", topic=" + topic +
                ", topicRole=" + topicRole +
                ", topicPermission=" + topicPermission +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicRolePermissions that = (TopicRolePermissions) o;
        return id == that.id &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(topicRole, that.topicRole) &&
                Objects.equals(topicPermission, that.topicPermission);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, topic, topicRole, topicPermission);
    }
}
