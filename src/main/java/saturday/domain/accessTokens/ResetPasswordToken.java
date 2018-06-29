package saturday.domain.accessTokens;

import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenTypeEmailConfirmationToken;
import saturday.domain.accessTokenTypes.AccessTokenTypeResetPasswordToken;

import javax.persistence.DiscriminatorValue;
import java.util.Date;

@javax.persistence.Entity
@DiscriminatorValue("2")
public class ResetPasswordToken extends AccessToken {
    public ResetPasswordToken() {
    }

    public ResetPasswordToken(Entity entity, String token) {
        super(
                token,
                new AccessTokenTypeResetPasswordToken(),
                entity
        );
    }

    public ResetPasswordToken(Entity entity, String tokenHash, Date expirationDate) {
        super(
                tokenHash,
                new AccessTokenTypeResetPasswordToken(),
                entity,
                expirationDate
        );
    }
}
