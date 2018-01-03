package saturday.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saturday.domain.AccessToken;
import saturday.domain.Entity;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {
    void deleteAccessTokenByToken(String token);

    AccessToken findByToken(String token);
}
