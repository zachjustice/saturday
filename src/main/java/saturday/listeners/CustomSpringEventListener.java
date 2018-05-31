package saturday.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.event.RegistrationEvent;
import saturday.domain.event.ResetPasswordEvent;
import saturday.domain.event.TopicMemberInviteEvent;
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
