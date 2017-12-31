package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.RegistrationConfirmationService;

import javax.mail.MessagingException;

@RestController
public class EmailController {

    private final EntityService entityService;
    private final RegistrationConfirmationService registrationConfirmationService;
    private final PermissionService permissionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EmailController(EntityService entityService, RegistrationConfirmationService registrationConfirmationService, PermissionService permissionService) {
        this.entityService = entityService;
        this.registrationConfirmationService = registrationConfirmationService;
        this.permissionService = permissionService;
    }

    @RequestMapping(value = "entities/{id}/resend_confirmation", method = RequestMethod.POST)
    public ResponseEntity<String> resendAccountConfirmationEmail(
            @PathVariable(value="id") int id
    ) throws ProcessingResourceException, AccessDeniedException, ResourceNotFoundException, MessagingException {

        Entity entity = entityService.findEntityById(id);
        if(!permissionService.canAccess(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        registrationConfirmationService.sendEmail(entity);

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}
