package saturday.domain;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "topic_content")
public class TopicContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name="topic_id", referencedColumnName = "id", nullable=false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name="creator_id", referencedColumnName = "id", nullable=false)
    private Entity creator;

    @Column(name = "description")
    private String description;

    @Column(name = "s3bucket")
    @NotEmpty
    private String s3bucket;

    @Column(name = "s3key")
    @NotEmpty
    private String s3key;

    @Column(name = "date_taken")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateTaken;

    @Column(name = "created", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    @Column(name = "modified", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modified;

    public String getS3url() {
        return "https://s3.amazonaws.com/" + getS3bucket() + "/" + getS3key();
    }

    public String getThumbnailUrl() {
        return "https://s3.amazonaws.com/" + getS3bucket() + "/resized-" + getS3key();
    }

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

    public Entity getCreator() {
        return creator;
    }

    public void setCreator(Entity creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getS3bucket() {
        return s3bucket;
    }

    public void setS3bucket(String s3bucket) {
        this.s3bucket = s3bucket;
    }

    public String getS3key() {
        return s3key;
    }

    public void setS3key(String s3key) {
        this.s3key = s3key;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    @Override
    public String toString() {
        return "TopicContent{" +
                "id=" + id +
                ", topic=" + topic + '\'' +
                ", creator=" + creator + '\'' +
                ", description='" + description + '\'' +
                ", s3bucket='" + s3bucket + '\'' +
                ", s3key='" + s3key + '\'' +
                ", created=" + created + '\'' +
                ", modified=" + modified +
                '}';
    }
}
