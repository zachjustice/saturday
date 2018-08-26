package saturday.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.publishers.SaturdayEventPublisher;
import saturday.services.EntityService;

@Component
public class EntityDelegate {
    private final SaturdayEventPublisher saturdayEventPublisher;
    private final EntityService entityService;
    private final AccessTokenDelegate accessTokenDelegate;

    @Autowired
    public EntityDelegate(
            SaturdayEventPublisher saturdayEventPublisher,
            EntityService entityService,
            AccessTokenDelegate accessTokenDelegate
    ) {
        this.saturdayEventPublisher = saturdayEventPublisher;
        this.entityService = entityService;
        this.accessTokenDelegate = accessTokenDelegate;
    }

    /**
     * Creates an entity. Publishes a registration event upon successful creation
     * and adds a new access token for the user. This token is returned so that
     * the entity controller can add the token to the response http headers.
     * @param entity The entity to create
     * @return The base64 encoded token consisting of the raw access token and the user's email.
     */
    public String save(Entity entity) {
        entity = entityService.saveEntity(entity);

        String base64EncodedToken = accessTokenDelegate.saveBearerToken(entity);

        saturdayEventPublisher.publishRegistrationEvent(entity);
        return base64EncodedToken;
    }
}
