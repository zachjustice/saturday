package saturday.delegates;

import com.adobe.xmp.impl.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.accessTokens.AccessToken;
import saturday.domain.accessTokens.BearerToken;
import saturday.services.AccessTokenService;
import saturday.utils.RandomString;

@Component
public class AccessTokenDelegate {
    private final AccessTokenService accessTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RandomString randomString;

    private static final int TOKEN_LENGTH = 21;

    @Autowired
    public AccessTokenDelegate(
            AccessTokenService accessTokenService,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.accessTokenService = accessTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.randomString = new RandomString();
    }

    public String saveBearerToken(Entity entity) {
        String token = randomString.nextString(TOKEN_LENGTH);
        String tokenHash = bCryptPasswordEncoder.encode(token);

        AccessToken accessToken = new BearerToken(entity, tokenHash);
        accessTokenService.save(accessToken);
        return Base64.encode(entity.getEmail() + ":" + token);
    }

}
