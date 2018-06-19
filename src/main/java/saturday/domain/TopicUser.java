package saturday.domain;

public class TopicUser extends TopicRole {

    public final static int TOPIC_ROLE_USER = 1;

    public TopicUser() {
        super(TOPIC_ROLE_USER, "USER");
    }
}
