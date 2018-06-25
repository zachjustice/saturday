package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.accessTokens.AccessToken;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {
    void deleteAccessTokenByToken(String token);
    void deleteAccessTokenByEntityAndType(Entity entity, AccessTokenType type);

    AccessToken findByToken(String token);
}
