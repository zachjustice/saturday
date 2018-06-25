package saturday.domain.accessTokens;

import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenTypeBearerToken;

import javax.persistence.DiscriminatorValue;
import java.util.UUID;

@javax.persistence.Entity
@DiscriminatorValue("3")
public class BearerToken extends AccessToken {
    public BearerToken(Entity entity) {
        super(
                UUID.randomUUID().toString(),
                new AccessTokenTypeBearerToken(),
                entity
        );
    }
}
