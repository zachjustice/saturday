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

    /**
     * Creates an entity. Publishes a registration event upon successful creation
     * and adds a new access token for the user. This token is returned so that
     * the entity controller can add the token to the response http headers.
     * @param entity The entity to create
     * @return The token for the new user.
     */
    public String save(Entity entity) {
        entity = entityService.saveEntity(entity);

        AccessToken token = new BearerToken(entity);
        accessTokenService.save(token);

        saturdayEventPublisher.publishRegistrationEvent(entity);
        return token.getToken();
    }
}
