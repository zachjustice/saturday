package saturday.services;

import saturday.domain.AccessToken;
import saturday.domain.AccessTokenType;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface AccessTokenService {
    void deleteAccessTokenByToken(String token);

    AccessToken findByToken(String token) ;
    AccessToken save(String email, int expirationDate, AccessTokenType accessTokenType) ;
    AccessToken save(AccessToken token) ;
}
