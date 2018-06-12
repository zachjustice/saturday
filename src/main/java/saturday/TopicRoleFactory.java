package saturday;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saturday.domain.TopicRole;

@Component
public class TopicRoleFactory {
    @Value("${saturday.topic.role.user}")
    private int TOPIC_ROLE_USER;

    public TopicRole createUser() {
        TopicRole userTopicRole = new TopicRole();
        userTopicRole.setId(TOPIC_ROLE_USER);

        return userTopicRole;
    }
}
