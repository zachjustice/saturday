package saturday.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "topic_role_permissions")
public class TopicRolePermission {
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

    @JsonAlias("isAllowed")
    @Column(name = "is_allowed", nullable = false)
    private Boolean isAllowed;

    @ManyToOne
    @JoinColumn(name="creator_id", referencedColumnName = "id", nullable=false, updatable = false)
    private Entity creator;

    @ManyToOne
    @JoinColumn(name="modifier_id", referencedColumnName = "id", nullable=false)
    private Entity modifier;

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

    @JsonAlias("isAllowed")
    public Boolean isAllowed() {
        return isAllowed;
    }

    @JsonAlias("isAllowed")
    public void setIsAllowed(Boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    public Entity getCreator() {
        return creator;
    }

    public void setCreator(Entity creator) {
        this.creator = creator;
    }

    public Entity getModifier() {
        return modifier;
    }

    public void setModifier(Entity modifier) {
        this.modifier = modifier;
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

    @Override
    public String toString() {
        return "TopicRolePermission{" +
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
        TopicRolePermission that = (TopicRolePermission) o;
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
