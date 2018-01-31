package saturday.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.ResetPasswordEvent;
import saturday.services.ResetPasswordService;

@Component
public class CustomSpringEventListener {
    private final ResetPasswordService resetPasswordService;

    @Autowired
    public CustomSpringEventListener(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @Async
    @EventListener
    public void onApplicationEvent(ResetPasswordEvent event) {
        resetPasswordService.sendEmail(event.getEntity());
    }
}
