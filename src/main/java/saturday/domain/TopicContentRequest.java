package saturday.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class TopicContentRequest {
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateTaken;
    private Integer topicId;
    private Integer creatorId;
    private MultipartFile file;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    @Override
    public String toString() {
        return "TopicContentRequest{" +
                ", description='" + description + '\'' +
                ", dateTaken=" + dateTaken +
                ", topicId=" + topicId +
                ", creatorId=" + creatorId +
                '}';
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
