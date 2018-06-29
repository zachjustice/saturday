package saturday.domain.accessTokens;

import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenTypeBearerToken;
import saturday.domain.accessTokenTypes.AccessTokenTypeEmailConfirmationToken;

import javax.persistence.DiscriminatorValue;
import java.util.Date;

@javax.persistence.Entity
@DiscriminatorValue("1")
public class EmailConfirmationToken extends AccessToken {
    public EmailConfirmationToken() {
    }

    public EmailConfirmationToken(Entity entity, String token) {
        super(
                token,
                new AccessTokenTypeEmailConfirmationToken(),
                entity
        );
    }

    public EmailConfirmationToken(Entity entity, String tokenHash, Date expirationDate) {
        super(
                tokenHash,
                new AccessTokenTypeEmailConfirmationToken(),
                entity,
                expirationDate
        );
    }
}
