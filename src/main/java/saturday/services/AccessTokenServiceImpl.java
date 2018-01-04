package saturday.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import saturday.domain.AccessToken;
import saturday.domain.AccessTokenType;
import saturday.exceptions.DuplicateResourceException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.AccessTokenRepository;
import saturday.utils.TokenAuthenticationUtils;

import java.util.Date;

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
    public AccessToken findByToken(String rawToken) {
        AccessToken token = accessTokenRepository.findByToken(rawToken);
        if(token == null) {
            throw new ResourceNotFoundException("Token does not exist!");
        }

        return token;
    }

    /**
     * Persist an access token using an email, expirationTime, and AccessTokenType.
     * @param email The email to be stored in the token
     * @param expirationTimeFromNow How many milliseconds from now the token should expire
     * @param accessTokenType what kind of the access token the token should be
     * @return The saved access token
     */
    @Override
    public AccessToken save(String email, int expirationTimeFromNow, AccessTokenType accessTokenType) {
        if(expirationTimeFromNow <= 0) {
            throw new IllegalArgumentException("Error persisting access token. ExpirationTimeFromNow must be greater than 0.");
        }

        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeFromNow);

        AccessToken accessToken = new AccessToken();
        accessToken.setType(accessTokenType);
        accessToken.setExpirationDate(expirationDate);
        accessToken.setToken(TokenAuthenticationUtils.createToken(email, expirationDate));

        return accessTokenRepository.save(accessToken);
    }

    /**
     * Persist an access token.
     * @param token The token with is expiry date to be saved
     * @return The saved access token
     */
    @Override
    public AccessToken save(AccessToken token) {
        if(StringUtils.isEmpty(token.getToken())) {
            throw new IllegalArgumentException("Error persisting access token. Null token.");
        }

        if(token.getExpirationDate() == null) {
            throw new IllegalArgumentException("Error persisting access token. Expiration date is null.");
        }

        if(token.getType() == null || token.getId() == null) {
            throw new IllegalArgumentException("Error persisting access token. Invalid access token type is null.");
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
