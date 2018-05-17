package saturday.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.event.RegistrationEvent;
import saturday.domain.event.ResetPasswordEvent;
import saturday.domain.event.TopicMemberInviteEvent;
import saturday.domain.oneSignal.OneSignalResponse;
import saturday.services.NotificationService;
import saturday.services.RegistrationConfirmationService;
import saturday.services.ResetPasswordService;

@Component
public class CustomSpringEventListener {
    private final ResetPasswordService resetPasswordService;
    private final RegistrationConfirmationService registrationConfirmationService;
    private final NotificationService notificationService;

    @Autowired
    public CustomSpringEventListener(ResetPasswordService resetPasswordService, RegistrationConfirmationService registrationConfirmationService, NotificationService notificationService) {
        this.resetPasswordService = resetPasswordService;
        this.registrationConfirmationService = registrationConfirmationService;
        this.notificationService = notificationService;
    }

    @Async
    @EventListener
    public void onApplicationEvent(RegistrationEvent event) {
        registrationConfirmationService.sendEmail(event.getEntity());
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
