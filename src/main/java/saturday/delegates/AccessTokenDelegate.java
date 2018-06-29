package saturday.delegates;

import com.adobe.xmp.impl.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.accessTokens.AccessToken;
import saturday.domain.accessTokens.BearerToken;
import saturday.domain.accessTokens.EmailConfirmationToken;
import saturday.domain.accessTokens.ResetPasswordToken;
import saturday.services.AccessTokenService;
import saturday.utils.RandomString;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AccessTokenDelegate {
    public static final int RESET_PASSWORD_TOKEN_LENGTH = 9;
    private final AccessTokenService accessTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RandomString randomString;

    private static final String TOKEN_PREFIX = "Bearer";

    private static final int TOKEN_LENGTH = 21;
    private RandomString randomUpperCaseAlphaNumericString;

    @Autowired
    public AccessTokenDelegate(
            AccessTokenService accessTokenService,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.accessTokenService = accessTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.randomString = new RandomString();
        randomUpperCaseAlphaNumericString = new RandomString(
                RandomString.upper + RandomString.digits
        );
    }

    public Optional<AccessToken> validateRawToken(String email, String rawToken, int accessTokenTypeId) {
        List<AccessToken> accessTokens = accessTokenService.findByEmailAndTypeId(email, accessTokenTypeId);

        return accessTokens
                .stream()
                .filter(accessToken -> bCryptPasswordEncoder.matches(rawToken, accessToken.getToken()))
                .findFirst();
    }

    public Optional<AccessToken> validate(String base64EncodedEmailAndToken, int accessTokenTypeId) {
        String emailAndToken;
        try {
            emailAndToken = Base64.decode(base64EncodedEmailAndToken);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        String[] emailAndTokenArr = emailAndToken.split(":");

        if (emailAndTokenArr.length != 2) {
            return Optional.empty();
        }

        String email = emailAndTokenArr[0];
        String rawToken = emailAndTokenArr[1];

        return validateRawToken(email, rawToken, accessTokenTypeId);
    }

    public String saveBearerToken(Entity entity) {
        String token = randomString.nextString(TOKEN_LENGTH);
        String tokenHash = bCryptPasswordEncoder.encode(token);

        AccessToken accessToken = new BearerToken(entity, tokenHash);
        accessTokenService.save(accessToken);
        return Base64.encode(entity.getEmail() + ":" + token);
    }

    public String saveEmailConfirmationToken(Entity entity, int tokenDuration) {
        String token = randomString.nextString(TOKEN_LENGTH);
        String tokenHash = bCryptPasswordEncoder.encode(token);
        Date expirationDate = new Date(System.currentTimeMillis() + tokenDuration);

        AccessToken accessToken = new EmailConfirmationToken(entity, tokenHash, expirationDate);
        accessTokenService.save(accessToken);
        return Base64.encode(entity.getEmail() + ":" + token);
    }

    public String saveResetPasswordToken(Entity entity, int tokenDuration) {
        Date expirationDate = new Date(System.currentTimeMillis() + tokenDuration);

        String token = randomUpperCaseAlphaNumericString.nextString(RESET_PASSWORD_TOKEN_LENGTH);
        String tokenHash = bCryptPasswordEncoder.encode(token);

        AccessToken accessToken = new ResetPasswordToken(entity, tokenHash, expirationDate);
        accessTokenService.save(accessToken);
        return token;
    }
}
