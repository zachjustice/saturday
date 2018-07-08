package saturday.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.accessTokens.AccessToken;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.AccessTokenRepository;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static saturday.utils.CommonUtils.getOrElse;

@Service()
public class AccessTokenService {
    private final AccessTokenRepository accessTokenRepository;
    @Value("${saturday.access-token-type.reset-password}")
    private int ACCESS_TOKEN_TYPE_RESET_PASSWORD;

    public AccessTokenService(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }

    /**
     * We delete tokens when users logout or the token is otherwise invalidated.
     *
     * @param token the raw bearer token to delete
     */
    @Transactional
    public void deleteAccessTokenByToken(String token) {
        accessTokenRepository.deleteAccessTokenByToken(token);
    }

    @Transactional
    public void deleteAccessTokenByEntityAndType(Entity entity, AccessTokenType type) {
        accessTokenRepository.deleteAccessTokenByEntityAndType(entity, type);
    }

    /**
     * Retrieve an access token. Useful for determining if an access token is valid.
     *
     * @param rawToken The raw bearer token to find
     * @return The access token object associated with the raw token
     */
    public AccessToken findByToken(String rawToken) {
        AccessToken token = accessTokenRepository.findByToken(rawToken);
        if (token == null) {
            throw new ResourceNotFoundException("Token, " + rawToken + ", does not exist!");
        }

        return token;
    }

    /**
     * Persist an access token.
     *
     * @param token The token with is expiry date to be saved
     * @return The saved access token
     */
    public AccessToken save(AccessToken token) {
        if (StringUtils.isEmpty(token.getToken())) {
            throw new IllegalArgumentException("Error persisting access token. Null token.");
        }

        if (token.getType() == null) {
            throw new IllegalArgumentException("Error persisting access token. Access token type is null.");
        }

        return accessTokenRepository.save(token);
    }

    /**
     * Constructs a string of random capitalized letters and digits with a length specified by the {@code num} argument
     *
     * @param num How long the returned string should be
     * @return A string of random letters
     */
    @Deprecated
    private String getRandomLettersAndDigits(int num) {
        Random random = new SecureRandom();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomLettersAndDigits = new StringBuilder(num);


        for (int i = 0; i < num; i++) {
            randomLettersAndDigits.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return randomLettersAndDigits.toString();
    }

    public List<AccessToken> findByEmailAndTypeId(String email, int typeId) {
        if (StringUtils.isEmpty(email)) {
            return Collections.emptyList();
        }

        return getOrElse(
                () ->  accessTokenRepository.findByEmailAndTypeId(email, typeId),
                Collections.emptyList()
        );
    }

    public void delete(AccessToken accessToken) {
        accessTokenRepository.delete(accessToken);
    }
}
