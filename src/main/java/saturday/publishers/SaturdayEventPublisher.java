package saturday.publishers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.RegistrationEvent;
import saturday.domain.ResetPasswordEvent;

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
}
