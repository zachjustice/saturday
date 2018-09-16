package saturday.domain;

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
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    protected int id;

    @Column(name = "name")
    protected String name;

    @Column(name = "description")
    protected String description;

    @ManyToOne
    @JoinColumn(name="owner_id", referencedColumnName = "id", nullable=false)
    protected Entity owner;

    @ManyToOne
    @JoinColumn(name="creator_id", referencedColumnName = "id", nullable=false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected Entity creator;

    @Column(name = "invite_link")
    protected String inviteLink;

    @Column(name = "invite_link_enabled")
    protected boolean inviteLinkEnabled;

    @Column(name = "created", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date created;

    @Column(name = "modified", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date modified;

    public Topic(String name, String description, Entity owner, Entity creator, Date created, Date modified) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creator = creator;
        this.created = created;
        this.modified = modified;
    }

    public Topic() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Entity getCreator() {
        return creator;
    }

    public void setCreator(Entity creator) {
        this.creator = creator;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    public boolean isInviteLinkEnabled() {
        return inviteLinkEnabled;
    }

    public void setInviteLinkEnabled(boolean inviteLinkEnabled) {
        this.inviteLinkEnabled = inviteLinkEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return id == topic.id &&
                inviteLinkEnabled == topic.inviteLinkEnabled &&
                Objects.equals(name, topic.name) &&
                Objects.equals(description, topic.description) &&
                Objects.equals(owner, topic.owner) &&
                Objects.equals(creator, topic.creator) &&
                Objects.equals(inviteLink, topic.inviteLink) &&
                Objects.equals(created, topic.created) &&
                Objects.equals(modified, topic.modified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, owner, creator, inviteLink, inviteLinkEnabled, created, modified);
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", creator=" + creator +
                ", inviteLink='" + inviteLink + '\'' +
                ", inviteLinkEnabled=" + inviteLinkEnabled +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
