package saturday.controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import saturday.delegates.AccessTokenDelegate;
import saturday.domain.Entity;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.accessTokenTypes.AccessTokenTypeBearerToken;
import saturday.domain.accessTokenTypes.AccessTokenTypeResetPasswordToken;
import saturday.domain.accessTokens.AccessToken;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.exceptions.UnauthorizedUserException;
import saturday.repositories.AccessTokenRepository;
import saturday.services.AccessTokenService;
import saturday.services.EntityService;
import saturday.utils.HTTPUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;

@RestController
public class AccessTokenController {
    private final EntityService entityService;
    private final AccessTokenService accessTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DefaultFacebookClient facebookClient;
    private final AccessTokenDelegate accessTokenDelegate;
    private final AccessTokenRepository accessTokenRepository;

    @Value("${spring.social.facebook.app-id}")
    private String FACEBOOK_APP_ID;
    @Value("${spring.social.facebook.app-secret}")
    private String FACEBOOK_APP_SECRET;
    @Value("${saturday.access-token-type.reset-password}")
    private int ACCESS_TOKEN_TYPE_RESET_PASSWORD;
    @Value("${saturday.access-token-type.email-confirmation}")
    private int ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION;
    @Value("${saturday.password-min-length}")
    private int PASSWORD_MIN_LENGTH;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AccessTokenController(
            EntityService entityService,
            AccessTokenService accessTokenService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            DefaultFacebookClient facebookClient,
            AccessTokenDelegate accessTokenDelegate,
            AccessTokenRepository accessTokenRepository
    ) {
        this.entityService = entityService;
        this.accessTokenService = accessTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.facebookClient = facebookClient;
        this.accessTokenDelegate = accessTokenDelegate;
        this.accessTokenRepository = accessTokenRepository;
    }

    /**
     * Validate a facebook access token and exchange the validated token with a saturday access token.
     * If the auth'ed user doesn't exist in our system, one is created using data from facebook's /me route using
     * the fb extended access token.
     *
     * @param response         We attach our token to the http response so the client can retrieve it
     * @param validationEntity The only field we use is fbAccessToken which we use to obtain an extended fb access token
     * @return The entity associated with the fb token
     */
    @RequestMapping(value = "/validate_access_token", method = RequestMethod.POST)
    public ResponseEntity<Entity> validateAccessToken(
            HttpServletResponse response,
            @RequestBody Entity validationEntity
    ) {
        String fbAccessToken;
        try {
            FacebookClient.AccessToken accessToken = facebookClient.obtainExtendedAccessToken(
                    FACEBOOK_APP_ID,
                    FACEBOOK_APP_SECRET,
                    validationEntity.getFbAccessToken()
            );

            fbAccessToken = accessToken.getAccessToken();
        } catch (FacebookOAuthException ex) {
            throw new AccessDeniedException("Failed to validate facebook auth token. Invalid access token.");
        } catch (FacebookException ex) {
            logger.info(ex.getLocalizedMessage());
            throw new ProcessingResourceException(
                    "Failed to validate facebook auth token. " +
                            "Error while attempting to validate client id."
            );
        }

        DefaultFacebookClient facebookClient25 = new DefaultFacebookClient(fbAccessToken, Version.VERSION_2_11);
        User fbUser = facebookClient25.fetchObject("me", User.class, Parameter.with("fields", "id,email,name,gender"));
        Entity entity = this.entityService.findEntityByEmail(fbUser.getEmail());

        if (entity == null) {
            // save unregistered users after the validating the fb auth token
            // but only if we haven't seen this user yet
            entity = new Entity();
            entity.setFbAccessToken(fbAccessToken);
            entity.setEmail(fbUser.getEmail());
            entity.setName(fbUser.getName());
            entity.setFbId(new Long(fbUser.getId()));

            entity = this.entityService.saveEntity(entity);
            logger.info("Entity doesn't exist. Created one: " + entity);
        } else {
            entity.setFbAccessToken(fbAccessToken);
        }

        String base64EncodedToken = accessTokenDelegate.saveBearerToken(entity);

        HTTPUtils.addAuthenticationHeader(response, base64EncodedToken);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    /**
     * Given an entity object populated with an email and password, create a saturday access token.
     *
     * @param response We attach our token to the http response so the client can retrieve it
     * @param entity     The user to auth
     * @return The auth'ed entity with the token in the header
     */
    @RequestMapping(value = "/access_token", method = RequestMethod.PUT)
    public ResponseEntity<Entity> getToken(HttpServletResponse response, @RequestBody Entity entity) throws BusinessLogicException, ProcessingResourceException, ResourceNotFoundException {

        if (StringUtils.isEmpty(entity.getEmail())) {
            throw new ProcessingResourceException("Invalid request. Empty email");
        }

        if (StringUtils.isEmpty(entity.getPassword())) {
            throw new ProcessingResourceException("Invalid request. Empty password");
        }

        String givenPassword = entity.getPassword();
        Entity existingEntity = entityService.findEntityByEmail(entity.getEmail());

        if (existingEntity == null || !bCryptPasswordEncoder.matches(givenPassword, existingEntity.getPassword())) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Interrupted sleep");
                throw new UnauthorizedUserException("Invalid password or email.");
            }

            throw new UnauthorizedUserException("Invalid password or email.");
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(existingEntity.getEmail(), null, emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String base64EncodedToken = accessTokenDelegate.saveBearerToken(existingEntity);

        HTTPUtils.addAuthenticationHeader(response, base64EncodedToken);

        return new ResponseEntity<>(existingEntity, HttpStatus.OK);
    }

    /**
     * Reset a user's password provided a valid access token and password
     *
     * @param rawToken      The reset password token
     * @param updatedEntity The entity object which holds the updated password
     * @return Success if the password was updated correctly
     */
    @RequestMapping(value = "/reset_password", method = RequestMethod.PUT)
    public ResponseEntity<String> resetPassword(
            @RequestParam(value = "token") String rawToken,
            @RequestBody Entity updatedEntity
    ) {
        if (StringUtils.isEmpty(updatedEntity.getEmail())) {
            throw new IllegalArgumentException("Email is empty or null");
        }

        // query the access token table by token to make sure the token is still valid
        // (i.e. hasn't already been used)
        AccessToken accessToken = accessTokenRepository
                .findByEmailAndTypeId(
                        updatedEntity.getEmail(),
                        ACCESS_TOKEN_TYPE_RESET_PASSWORD
                )
                .stream()
                .filter(token -> bCryptPasswordEncoder.matches(rawToken, token.getToken()))
                .findFirst()
                .orElseThrow(AccessDeniedException::new);

        // We've validated the user is authenticated. Now make sure the request has the required fields.
        if (
            StringUtils.isEmpty(updatedEntity.getPassword())
            || updatedEntity.getPassword().length() < PASSWORD_MIN_LENGTH
        ) {
            throw new IllegalArgumentException(
                    "Password field must be at least " + PASSWORD_MIN_LENGTH + " characters."
            );
        }

        Entity entity = accessToken.getEntity();

        if (entity == null) {
            logger.error(
                    "Attempted to reset password. Token was valid but the entity was null: " + accessToken
            );
            throw new ResourceNotFoundException("Unable to reset password.");
        }

        // update the user's status to the new password
        entity.setPassword(bCryptPasswordEncoder.encode(updatedEntity.getPassword()));
        entityService.updateEntity(entity);

        // delete all reset password tokens and all other current bearer tokens if its valid and we've successfully updated the user
        accessTokenService.deleteAccessTokenByEntityAndType(entity, new AccessTokenTypeResetPasswordToken());
        accessTokenService.deleteAccessTokenByEntityAndType(entity, new AccessTokenTypeBearerToken());

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}
