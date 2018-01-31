package saturday.domain;

import org.springframework.context.ApplicationEvent;

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
