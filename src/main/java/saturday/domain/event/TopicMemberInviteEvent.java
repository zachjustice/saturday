package saturday.domain.event;

import org.springframework.context.ApplicationEvent;
import saturday.domain.TopicMember;

public class TopicMemberInviteEvent extends ApplicationEvent {
    private final TopicMember invitedTopicMember;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public TopicMemberInviteEvent(Object source, TopicMember topicMember) {
        super(source);
        this.invitedTopicMember = topicMember;
    }

    public TopicMember getInvitedTopicMember() {
        return invitedTopicMember;
    }
}
