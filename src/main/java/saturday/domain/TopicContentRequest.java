package saturday.domain;

public class TopicContentRequest {
    private String id;
    private String data;
    private String description;
    private Integer topic;
    private Integer creator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getTopic() {
        return topic;
    }

    public void setTopic(Integer topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "TopicContentRequest{" +
                "data='" + data + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
