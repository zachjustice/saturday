package saturday.domain.events;

import org.springframework.context.ApplicationEvent;
import saturday.domain.Entity;

public class RegistrationEvent extends ApplicationEvent {

    private Entity entity;

    public RegistrationEvent(Object source, Entity entity) {
        super(source);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
