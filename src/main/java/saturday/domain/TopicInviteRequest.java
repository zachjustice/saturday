package saturday.domain;

public class TopicInviteRequest {
    private int id;
    private int inviterId;
    private int inviteeId;
    private int topicId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInviterId() {
        return inviterId;
    }

    public void setInviterId(int inviterId) {
        this.inviterId = inviterId;
    }

    public int getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(int inviteeId) {
        this.inviteeId = inviteeId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "TopicInviteRequest{" +
                "id=" + id +
                ", inviterId=" + inviterId +
                ", inviteeId=" + inviteeId +
                ", topicId=" + topicId +
                '}';
    }
}
