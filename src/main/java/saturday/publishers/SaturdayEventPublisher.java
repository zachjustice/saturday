package saturday.publishers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.*;
import saturday.domain.event.RegistrationEvent;
import saturday.domain.event.ResetPasswordEvent;
import saturday.domain.event.TopicMemberInviteEvent;
import saturday.domain.topicMemberStatuses.TopicMember;

@Component
public class SaturdayEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SaturdayEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async
    public void publishRegistrationEvent(final Entity entity) {
        RegistrationEvent registrationEvent  = new RegistrationEvent(this, entity);
        applicationEventPublisher.publishEvent(registrationEvent);
    }

    @Async
    public void publishResetPasswordEvent(final Entity entity) {
        ResetPasswordEvent resetPasswordEvent = new ResetPasswordEvent(this, entity);
        applicationEventPublisher.publishEvent(resetPasswordEvent);
    }

    @Async
    public void publishInviteEvent(final TopicMember invitedTopicMember) {
        TopicMemberInviteEvent inviteEvent = new TopicMemberInviteEvent(this, invitedTopicMember);
        applicationEventPublisher.publishEvent(inviteEvent);
    }
}
