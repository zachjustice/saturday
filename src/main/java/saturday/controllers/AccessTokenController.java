package saturday.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import saturday.domain.Entity;

@RestController
public class AccessTokenController {
    /*
      [] pass user info and access token to /validate_access_token route
      [] /validate_access_token route validates the access token with server to server REST call using restfb
      [] generate a JWT to send back to the user. JWT is stored in DB
      [] each time a request is made, exchange JWT and invalidate the previous
    */
    @RequestMapping(value = "/validate_access_token", method = RequestMethod.POST)
    public ResponseEntity<Entity> validateAccessToken(@RequestBody Entity entity) {
        return null;
    }

}
