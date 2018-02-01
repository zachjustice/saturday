package saturday.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.RegistrationEvent;
import saturday.domain.ResetPasswordEvent;
import saturday.services.RegistrationConfirmationService;
import saturday.services.ResetPasswordService;

@Component
public class CustomSpringEventListener {
    private final ResetPasswordService resetPasswordService;
    private final RegistrationConfirmationService registrationConfirmationService;

    @Autowired
    public CustomSpringEventListener(ResetPasswordService resetPasswordService, RegistrationConfirmationService registrationConfirmationService) {
        this.resetPasswordService = resetPasswordService;
        this.registrationConfirmationService = registrationConfirmationService;
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
}
