package saturday.domain;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CreateTopicRequest extends Topic {
    private List<String> initialTopicMemberEmails;
    private Topic topic;

    public CreateTopicRequest() {
        super();
    }

    public CreateTopicRequest(String name, String description, Entity owner, Entity creator, Date created, Date modified) {
        super(name, description, owner, creator, created, modified);
    }

    public Topic getTopic() {
        if (this.topic == null) {
            this.topic = new Topic(
                    name,
                    description,
                    owner,
                    creator,
                    created,
                    modified
            );
        }

        return this.topic;
    }

    public List<String> getInitialTopicMemberEmails() {
        return initialTopicMemberEmails;
    }

    @Override
    public String toString() {
        return "CreateTopicRequest{" +
                "initialTopicMemberEmails=" + initialTopicMemberEmails.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateTopicRequest that = (CreateTopicRequest) o;
        return Objects.equals(initialTopicMemberEmails, that.initialTopicMemberEmails) &&
                Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {

        return Objects.hash(initialTopicMemberEmails, topic);
    }
}
