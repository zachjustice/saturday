package saturday.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.accessTokens.AccessToken;
import saturday.domain.Entity;
import saturday.domain.accessTokens.BearerToken;
import saturday.publishers.SaturdayEventPublisher;
import saturday.services.AccessTokenService;
import saturday.services.EntityService;

import java.util.UUID;

@Component
public class EntityDelegate {
    private final SaturdayEventPublisher saturdayEventPublisher;
    private final EntityService entityService;
    private final AccessTokenService accessTokenService;

    @Autowired
    public EntityDelegate(
            SaturdayEventPublisher saturdayEventPublisher,
            EntityService entityService,
            AccessTokenService accessTokenService
    ) {
        this.saturdayEventPublisher = saturdayEventPublisher;
        this.entityService = entityService;
        this.accessTokenService = accessTokenService;
    }

    public Entity save(Entity entity) {
        entity = entityService.saveEntity(entity);
        String uuid = UUID.randomUUID().toString();
        AccessToken token = new BearerToken(entity, uuid);

        accessTokenService.save(token);
        saturdayEventPublisher.publishRegistrationEvent(entity);
        return entity;
    }
}
