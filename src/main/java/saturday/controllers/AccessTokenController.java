package saturday.controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import saturday.domain.AccessToken;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.AccessTokenService;
import saturday.services.EntityService;
import saturday.utils.HTTPUtils;
import saturday.utils.TokenAuthenticationUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@RestController
public class AccessTokenController {
    private final EntityService entityService;
    private final AccessTokenService accessTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DefaultFacebookClient facebookClient;

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
            DefaultFacebookClient facebookClient
    ) {
        this.entityService = entityService;
        this.accessTokenService = accessTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.facebookClient = facebookClient;
    }

    /**
     * Validate a facebook access token and exchange the validated token with a saturday access token.
     * If the auth'ed user doesn't exist in our system, one is created using data from facebook's /me route using
     * the fb extended access token.
     * @param response We attach our token to the http response so the client can retrieve it
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
        } catch(FacebookException ex) {
           logger.info(ex.getLocalizedMessage());
           throw new ProcessingResourceException(
                   "Failed to validate facebook auth token. " +
                   "Error while attempting to validate client id."
           );
        }

        DefaultFacebookClient facebookClient25 = new DefaultFacebookClient(fbAccessToken, Version.VERSION_2_11);
        User fbUser = facebookClient25.fetchObject("me", User.class, Parameter.with("fields", "id,email,name,gender"));
        Entity entity = this.entityService.findEntityByEmail(fbUser.getEmail());

        if(entity == null) {
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

        // make sure entity has a saturday-specific tokens we can put in the response headers
        if(StringUtils.isEmpty(entity.getToken())) {
            entity.setToken(TokenAuthenticationUtils.createToken(fbUser.getEmail()));
        }

        HTTPUtils.addAuthenticationHeader(response, entity.getToken());
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    /**
     * Given an entity object populated with an email and password, create a saturday access token.
     * @param response We attach our token to the http response so the client can retrieve it
     * @param user The user to auth
     * @return The auth'ed entity with the token in the header
     */
    @RequestMapping(value = "/access_token", method = RequestMethod.PUT)
    public ResponseEntity<Entity> getToken(HttpServletResponse response, @RequestBody Entity user) throws BusinessLogicException, ProcessingResourceException, ResourceNotFoundException {

        if(StringUtils.isEmpty(user.getEmail())) {
           throw new ProcessingResourceException("Invalid request. Empty email");
        }

        if(StringUtils.isEmpty(user.getPassword())) {
            throw new ProcessingResourceException("Invalid request. Empty password");
        }

        String givenPassword = user.getPassword();
        Entity actualUser = entityService.findEntityByEmail(user.getEmail());

        if(actualUser == null || !bCryptPasswordEncoder.matches(givenPassword, actualUser.getPassword())) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Interrupted sleep");
                throw new ProcessingResourceException("Invalid password or email.");
            }

            throw new ProcessingResourceException("Invalid password or email.");
        }

        String token = TokenAuthenticationUtils.createToken(actualUser.getEmail());
        actualUser.setToken(token);
        entityService.updateEntity(actualUser);

        HTTPUtils.addAuthenticationHeader(response, token);

        return new ResponseEntity<>(actualUser, HttpStatus.OK);
    }

    /**
     * Reset a user's password provided a valid access token and password
     * @param token The reset password token
     * @param updatedEntity The entity object which holds the updated password
     * @return Success if the password was updated correctly
     */
    @RequestMapping(value = "/reset_password", method = RequestMethod.PUT)
    public ResponseEntity<String> resetPassword(
            @RequestParam(value="token") String token,
            @RequestBody Entity updatedEntity
    ) {

        String email;
        try {
            email = TokenAuthenticationUtils.validateToken(token);
        } catch(ExpiredJwtException ex) {
            throw new AccessDeniedException("Failed to reset password due to expired token.");
        } catch( MalformedJwtException ex) {
            throw new AccessDeniedException("Failed to reset password due to malformed token.");
        }

        // query the access token table by token to make sure the token is still valid
        // (i.e. hasn't already been used)
        AccessToken existing;
        try{
            existing = accessTokenService.findByToken(token);
        } catch (ResourceNotFoundException e) {
            throw new AccessDeniedException("Access token is invalid.");
        }

        // Make sure they're using the correct kind of token
        // i.e. use a normal auth token to reset the password
        if(existing.getType().getId() != ACCESS_TOKEN_TYPE_RESET_PASSWORD) {
            throw new AccessDeniedException("Access token is invalid.");
        }

        // We've validated the user is authenticated. Now make sure the request has the required fields.
        if(StringUtils.isEmpty(updatedEntity.getPassword()) || updatedEntity.getPassword().length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalArgumentException("Password field must be at least " + PASSWORD_MIN_LENGTH + " characters.");
        }

        // update the user's status to the new password
        Entity entity = entityService.findEntityByEmail(email);
        if(entity == null) {
            logger.error("Attempted to reset password. The token was valid but the user, " + email + ", no longer exists.");
            throw new ResourceNotFoundException("Unable to reset password.");
        }

        entity.setPassword(updatedEntity.getPassword());
        entityService.updateEntity(entity);

        // delete the access token if its valid and we've successfully updated the user
        accessTokenService.deleteAccessTokenByToken(existing.getToken());

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}
