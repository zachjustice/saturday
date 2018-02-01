package saturday.domain;

import org.springframework.context.ApplicationEvent;

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
