package saturday.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "topic_invites")
public class TopicInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name="inviter_id", referencedColumnName = "id", nullable=false)
    private Entity inviter;

    @ManyToOne
    @JoinColumn(name="invitee_id", referencedColumnName = "id", nullable=false)
    private Entity invitee;

    @ManyToOne
    @JoinColumn(name="topic_id", referencedColumnName = "id", nullable=false)
    private Topic topic;

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

    public Entity getInviter() {
        return inviter;
    }

    public void setInviter(Entity inviter) {
        this.inviter = inviter;
    }

    public Entity getInvitee() {
        return invitee;
    }

    public void setInvitee(Entity invitee) {
        this.invitee = invitee;
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

    @Override
    public String toString() {
        return "TopicInvite{" +
                "id=" + id +
                ", inviter=" + inviter +
                ", invitee=" + invitee +
                ", topic=" + topic +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
