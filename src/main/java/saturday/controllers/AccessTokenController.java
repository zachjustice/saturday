package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import saturday.domain.Entity;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.services.EntityServiceImpl;
import saturday.utils.HTTPUtils;
import saturday.utils.TokenAuthenticationUtils;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccessTokenController {
    private final EntityServiceImpl entityService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AccessTokenController(EntityServiceImpl entityService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.entityService = entityService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /*
      [x] pass user info and access token to /validate_access_token route
      [] /validate_access_token route validates the access token with server to server REST call using restfb
      [] generate a JWT to send back to the user. JWT is stored in DB
      [] each time a request is made, exchange JWT and invalidate the previous
    */
    @RequestMapping(value = "/validate_access_token", method = RequestMethod.POST)
    public ResponseEntity<Entity> validateAccessToken(HttpServletResponse response, @RequestBody Entity validationEntity) throws BusinessLogicException {
        Entity entity = this.entityService.findEntityByEmail(validationEntity.getEmail());
        logger.info("Attempt to find existing entity: " + entity);

        // TODO validate fb access token before saving unregistered users
        // save unregistered users
        if(entity == null) {
            entity = this.entityService.saveEntity(validationEntity);
            logger.info("Entity doesn't exist. Created one: " + entity);
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
            throw new ProcessingResourceException("Invalid password or email.");
        }

        String token = TokenAuthenticationUtils.createToken(actualUser.getEmail());
        user.setToken(token);
        entityService.updateEntity(actualUser, user);

        HTTPUtils.addAuthenticationHeader(response, token);

        return new ResponseEntity<>(actualUser, HttpStatus.OK);
    }
}
