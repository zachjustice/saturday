package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.accessTokens.AccessToken;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {
    void deleteAccessTokenByToken(String token);
    void deleteAccessTokenByEntityAndType(Entity entity, AccessTokenType type);

    AccessToken findByToken(String token);

    @Query(
            value = "select at.id, at.type_id, at.token, at.entity_id, at.expiration_date from access_tokens at join entities e on e.id = at.entity_id where e.email = :email and type_id = :type_id",
            nativeQuery = true
    )
    List<AccessToken> findByEmailAndTypeId(
            @Param("email") String email,
            @Param("type_id") int typeId
    );
}
