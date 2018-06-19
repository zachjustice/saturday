package saturday.domain;

public class TopicAdmin extends TopicRole {

    public final static int TOPIC_ROLE_ADMIN = 2;

    public TopicAdmin() {
        super(TOPIC_ROLE_ADMIN, "ADMIN");
    }
}
