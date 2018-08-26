package saturday.delegates;

import com.adobe.xmp.impl.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import saturday.domain.Entity;
import saturday.domain.accessTokens.AccessToken;
import saturday.domain.accessTokens.BearerToken;
import saturday.domain.accessTokens.EmailConfirmationToken;
import saturday.domain.accessTokens.ResetPasswordToken;
import saturday.services.AccessTokenService;
import saturday.utils.RandomString;

import java.util.Date;
import java.util.Optional;

import static java.util.Base64.getEncoder;

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

    public Optional<AccessToken> validate(String base64EncodedToken) {
        if (base64EncodedToken == null) {
            return Optional.empty();
        }

        if (StringUtils.isEmpty(base64EncodedToken.trim())) {
            return Optional.empty();
        }

        String emailAndToken;
        try {
            emailAndToken = Base64.decode(base64EncodedToken);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        TokenForSerialization token;
        try {
            token = new Gson().fromJson(emailAndToken, TokenForSerialization.class);
        } catch (JsonSyntaxException ex) {
            return Optional.empty();
        }

        if (token == null) {
            return Optional.empty();
        }

        Optional<AccessToken> optionalAccessToken = accessTokenService.findById(token.getId());
        return optionalAccessToken.flatMap(accessToken ->
                    bCryptPasswordEncoder.matches(token.getToken(), accessToken.getToken()) ?
                        Optional.of(accessToken) :
                        Optional.empty()
                );
    }

    public String saveBearerToken(Entity entity) {
        String token = randomString.nextString(TOKEN_LENGTH);
        String tokenHash = bCryptPasswordEncoder.encode(token);

        AccessToken accessToken = new BearerToken(entity, tokenHash);
        accessTokenService.save(accessToken);
        return base64EncodeAccessToken(accessToken.getId(), accessToken.getEntity().getEmail(), token);
    }

    public String saveEmailConfirmationToken(Entity entity, int tokenDuration) {
        String token = randomString.nextString(TOKEN_LENGTH);
        String tokenHash = bCryptPasswordEncoder.encode(token);
        Date expirationDate = new Date(System.currentTimeMillis() + tokenDuration);

        AccessToken accessToken = new EmailConfirmationToken(entity, tokenHash, expirationDate);
        accessTokenService.save(accessToken);
        return base64EncodeAccessToken(accessToken.getId(), accessToken.getEntity().getEmail(), token);
    }

    public String saveResetPasswordToken(Entity entity, int tokenDuration) {
        Date expirationDate = new Date(System.currentTimeMillis() + tokenDuration);

        String token = randomUpperCaseAlphaNumericString.nextString(RESET_PASSWORD_TOKEN_LENGTH);
        String tokenHash = bCryptPasswordEncoder.encode(token);

        AccessToken accessToken = new ResetPasswordToken(entity, tokenHash, expirationDate);
        accessTokenService.save(accessToken);
        return token;
    }

    private String base64EncodeAccessToken(int id, String email, String rawToken) {
        if (email == null) {
            throw new IllegalArgumentException("AccessToken id cannot be null");
        }

        if (rawToken == null) {
            throw new IllegalArgumentException("AccessToken token cannot be null");
        }

        TokenForSerialization tokenForSerialization = new TokenForSerialization(
                id,
                email,
                rawToken
        );

        java.util.Base64.Encoder encoder = getEncoder();
        return encoder.encodeToString(new Gson().toJson(tokenForSerialization).getBytes());
    }

    private class TokenForSerialization {
        private int id;
        private String email;
        private String token;

        public TokenForSerialization() {}

        public TokenForSerialization(int id, String email, String token) {
            this.id = id;
            this.email = email;
            this.token = token;
        }

        public int getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getToken() {
            return token;
        }
    }
}
