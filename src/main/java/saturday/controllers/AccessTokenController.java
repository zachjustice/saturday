package saturday.controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookOAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.services.EntityServiceImpl;
import saturday.utils.HTTPUtils;
import saturday.utils.TokenAuthenticationUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@RestController
public class AccessTokenController {
    private final EntityServiceImpl entityService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DefaultFacebookClient facebookClient;

    @Value("${spring.social.facebook.app-id}")
    private String FACEBOOK_APP_ID;
    @Value("${spring.social.facebook.app-secret}")
    private String FACEBOOK_APP_SECRET;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AccessTokenController(
            EntityServiceImpl entityService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            DefaultFacebookClient facebookClient
    ) {
        this.entityService = entityService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.facebookClient = facebookClient;
    }

    /*
      [x] pass user info and access token to /validate_access_token route
      [] /validate_access_token route validates the access token with server to server REST call using restfb
      [] generate a JWT to send back to the user. JWT is stored in DB
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

    @RequestMapping(value = "/access_token", method = RequestMethod.PUT)
    public ResponseEntity<Entity> getToken(HttpServletResponse response, @RequestBody Entity user) throws BusinessLogicException, ProcessingResourceException {

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
        entityService.updateEntity(actualUser, user);

        HTTPUtils.addAuthenticationHeader(response, token);

        return new ResponseEntity<>(actualUser, HttpStatus.OK);
    }
}
