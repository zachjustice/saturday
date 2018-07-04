package saturday.domain;

import java.util.Arrays;
import java.util.Date;

public class CreateTopicRequest extends Topic {
    private String[] initialTopicMemberEmails;
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

    public String[] getInitialTopicMemberEmails() {
        return initialTopicMemberEmails;
    }

    @Override
    public String toString() {
        return "CreateTopicRequest{" +
                "initialTopicMemberEmails=" + Arrays.toString(initialTopicMemberEmails) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateTopicRequest that = (CreateTopicRequest) o;
        return Arrays.equals(initialTopicMemberEmails, that.initialTopicMemberEmails);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(initialTopicMemberEmails);
    }
}
