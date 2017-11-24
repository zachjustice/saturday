package saturday.domain;

public class TopicMemberRequest {
    private int entityId;
    private int topicId;

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "TopicMemberRequest{" +
                "entityId=" + entityId +
                ", topicId=" + topicId +
                '}';
    }
}
