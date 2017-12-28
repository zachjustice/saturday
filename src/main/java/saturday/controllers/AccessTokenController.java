package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import saturday.domain.Entity;
import saturday.exceptions.BusinessLogicException;
import saturday.services.EntityServiceImpl;
import saturday.utils.TokenAuthenticationUtils;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccessTokenController {
    private final EntityServiceImpl entityService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AccessTokenController(EntityServiceImpl entityService) {
        this.entityService = entityService;
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

        TokenAuthenticationUtils.addAuthentication(response, entity.getEmail());
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

}
