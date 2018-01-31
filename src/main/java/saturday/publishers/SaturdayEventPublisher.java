package saturday.publishers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import saturday.domain.ResetPasswordEvent;
import saturday.domain.Entity;

@Component
public class SaturdayEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SaturdayEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async
    public void publishResetPasswordEvent(final Entity entity) {
        logger.info("Publishing custom event. ");
        ResetPasswordEvent resetPasswordEvent = new ResetPasswordEvent(this, entity);
        applicationEventPublisher.publishEvent(resetPasswordEvent);
    }
}
