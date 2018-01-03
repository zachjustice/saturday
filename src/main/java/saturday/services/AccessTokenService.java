package saturday.services;

import saturday.domain.AccessToken;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface AccessTokenService {
    void deleteAccessTokenByToken(String token);

    AccessToken findByToken(String token) ;
    AccessToken save(AccessToken token) ;
}
