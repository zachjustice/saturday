package saturday.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.events.RegistrationEvent;
import saturday.domain.events.ResetPasswordEvent;
import saturday.domain.events.TopicMemberInviteEvent;
import saturday.services.ConfirmEmailService;
import saturday.services.NotificationService;
import saturday.services.ResetPasswordService;

@Component
public class CustomSpringEventListener {
    private final ResetPasswordService resetPasswordService;
    private final ConfirmEmailService confirmEmailService;
    private final NotificationService notificationService;

    @Autowired
    public CustomSpringEventListener(ResetPasswordService resetPasswordService, ConfirmEmailService confirmEmailService, NotificationService notificationService) {
        this.resetPasswordService = resetPasswordService;
        this.confirmEmailService = confirmEmailService;
        this.notificationService = notificationService;
    }

    // Note, break out these listeners if it becomes too large.
    // Or just move it to AWS Kinesis + Lambda or something
    @Async
    @EventListener
    public void onApplicationEvent(RegistrationEvent event) {
        confirmEmailService.sendEmail(event.getEntity());
    }

    @Async
    @EventListener
    public void onApplicationEvent(ResetPasswordEvent event) {
        resetPasswordService.sendEmail(event.getEntity());
    }

    @Async
    @EventListener
    public void onApplicationEvent(TopicMemberInviteEvent inviteEvent) {
        this.notificationService.send(inviteEvent.getInvitedTopicMember());
    }
}
