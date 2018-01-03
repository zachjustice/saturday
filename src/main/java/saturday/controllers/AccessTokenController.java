package saturday.controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookOAuthException;
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
     * Validate a facebook access token and exchange the validated token with a saturday access token
     * If the auth'ed user doesn't exist in our system, create one using the provided data
     * @param response We attach our token to the http response so the client can retrieve it
     * @param validationEntity The entity, either created or retrieved, associated with the user
     * @return The entity associated with the fb token
     * @throws AccessDeniedException If the fb token fails validation
     */
    @RequestMapping(value = "/validate_access_token", method = RequestMethod.POST)
    public ResponseEntity<Entity> validateAccessToken(
            HttpServletResponse response,
            @RequestBody Entity validationEntity
    ) throws BusinessLogicException, AccessDeniedException, ProcessingResourceException {
        Entity entity = this.entityService.findEntityByEmail(validationEntity.getEmail());
        logger.info("fb id: " + FACEBOOK_APP_ID + ", fb token: " + FACEBOOK_APP_SECRET);
        logger.info("Attempt to find existing entity: " + entity);

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

        // save unregistered users after the fb auth token authenticated
        // but only if we haven't seen that user yet
        if(entity == null) {
            entity = this.entityService.saveEntity(validationEntity);
            logger.info("Entity doesn't exist. Created one: " + entity);
        }

        // Set fb access token
        entity.setFbAccessToken(fbAccessToken);

        // make sure entity has saturday-specific tokens
        if(StringUtils.isEmpty(entity.getToken())) {
            entity.setToken(TokenAuthenticationUtils.createToken(entity.getEmail()));
        }

        HTTPUtils.addAuthenticationHeader(response, entity.getToken());
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    /**
     * Given an entity object populated with an email and password, create a saturday access token.
     * @param response We attach our token to the http response so the client can retrieve it
     * @param user The user to auth
     * @return The auth'ed entity with the token in the header
     * @throws ProcessingResourceException If the request is invalid or we can't validate the password
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
        user.setToken(token);
        entityService.updateEntity(user);

        HTTPUtils.addAuthenticationHeader(response, token);

        return new ResponseEntity<>(actualUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/email_confirmation", method = RequestMethod.GET)
    public ResponseEntity<String> confirmEmail(
            @RequestParam(value="token") String token
    ) throws ProcessingResourceException, AccessDeniedException, BusinessLogicException, ResourceNotFoundException {

        String email;
        try {
            email = TokenAuthenticationUtils.validateToken(token);
        } catch(ExpiredJwtException ex) {
            throw new ProcessingResourceException("Failed to confirm email due to expired token.");
        } catch( MalformedJwtException ex) {
            throw new ProcessingResourceException("Failed to confirm email due to malformed token.");
        }

        // query the access token table by token to make sure its still valid
        AccessToken existing;
        try{
            existing = accessTokenService.findByToken(token);
        } catch (ResourceNotFoundException e) {
            throw new AccessDeniedException("Access token is not valid.");
        }

        // delete the access token if its valid
        accessTokenService.deleteAccessTokenByToken(existing.getToken());

        // update the user's status to reflect the confirmed email
        Entity entity = entityService.findEntityByEmail(email);
        entity.setEmailConfirmed(true);

        entityService.updateEntity(entity);

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}
