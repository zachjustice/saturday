package saturday.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import saturday.domain.AccessToken;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.DuplicateResourceException;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.AccessTokenRepository;

@Service("accessTokenService")
public class AccessTokenServiceImpl implements AccessTokenService {
    private final AccessTokenRepository accessTokenRepository;

    public AccessTokenServiceImpl(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }

    /**
     * We delete tokens when users logout or the token is otherwise invalidated.
     * @param token the raw bearer token to delete
     */
    @Override
    @Transactional
    public void deleteAccessTokenByToken(String token) {
        accessTokenRepository.deleteAccessTokenByToken(token);
    }

    /**
     * Retrieve an access token. Useful for determining if an access token is valid.
     * @param rawToken The raw bearer token to find
     * @return The access token object associated with the raw token
     */
    @Override
    public AccessToken findByToken(String rawToken) throws ResourceNotFoundException {
        AccessToken token = accessTokenRepository.findByToken(rawToken);
        if(token == null) {
            throw new ResourceNotFoundException("Token does not exist!");
        }

        return token;
    }

    /**
     * Persist an access token.
     * @param token The token with is expiry date to be saved
     * @return The saved access token
     * @throws BusinessLogicException If the token is already being used
     * @throws ProcessingResourceException If the given token is null
     */
    @Override
    public AccessToken save(AccessToken token) {
        if(StringUtils.isEmpty(token.getToken())) {
            throw new IllegalArgumentException("Error persisting access token. Null token.");
        }

        AccessToken existing = null;
        try {
            existing = findByToken(token.getToken());
        } catch(ResourceNotFoundException ignored) {
        }

        if(existing != null) {
            throw new DuplicateResourceException("Error persisting access token.");
        }

        return accessTokenRepository.save(token);
    }
}
