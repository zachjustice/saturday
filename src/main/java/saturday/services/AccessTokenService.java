package saturday.services;

import saturday.domain.AccessToken;
import saturday.domain.Topic;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface AccessTokenService {
    void deleteAccessTokenByToken(String token);

    AccessToken findByToken(String token) throws ResourceNotFoundException;
    AccessToken save(AccessToken token) throws BusinessLogicException, ProcessingResourceException, ResourceNotFoundException;
}
