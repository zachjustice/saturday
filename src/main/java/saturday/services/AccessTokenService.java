package saturday.services;

import saturday.domain.AccessToken;
import saturday.domain.AccessTokenType;
import saturday.domain.Entity;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface AccessTokenService {
    void deleteAccessTokenByToken(String token);

    AccessToken findByToken(String token) ;
    AccessToken save(Entity entity, int expirationDate, AccessTokenType accessTokenType) ;
    AccessToken save(AccessToken token) ;
}
