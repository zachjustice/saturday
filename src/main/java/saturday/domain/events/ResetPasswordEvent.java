package saturday.domain.events;

import org.springframework.context.ApplicationEvent;
import saturday.domain.Entity;

public class ResetPasswordEvent extends ApplicationEvent {

    private Entity entity;

    public ResetPasswordEvent(Object source, Entity entity) {
        super(source);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
