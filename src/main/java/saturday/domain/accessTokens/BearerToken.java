package saturday.domain.accessTokens;

import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenTypeBearerToken;

import java.util.Date;

public class BearerToken extends AccessToken {
    public BearerToken(Entity entity, String token) {
        super(token, new AccessTokenTypeBearerToken(), entity);
    }
}
