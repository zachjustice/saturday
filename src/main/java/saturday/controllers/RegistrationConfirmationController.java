package saturday.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.BusinessLogicException;
import saturday.services.RegistrationConfirmationService;

@Controller
public class RegistrationConfirmationController {

    private final RegistrationConfirmationService registrationConfirmationService;

    public RegistrationConfirmationController(RegistrationConfirmationService registrationConfirmationService) {
        this.registrationConfirmationService = registrationConfirmationService;
    }

    @RequestMapping(value = "/registration_confirmation", method = RequestMethod.PUT)
    public ResponseEntity<Entity> registrationConfirmation(
            @RequestParam(value = "token") String token
    ) {
        Entity entity;

        try {
            entity = registrationConfirmationService.validateRegistrationConfirmationToken(token);
        } catch (ExpiredJwtException ex) {
            String error = "Confirmation emails are only valid for 24 hours.";
            throw new BusinessLogicException(error);
        } catch (MalformedJwtException ex) {
            String error = "Make sure to click 'Confirm your Email' or copy and paste the whole link into your browser.";
            throw new BusinessLogicException(error);
        } catch (AccessDeniedException ex) {
            String error = "Invalid token.";
            throw new AccessDeniedException(error);
        }

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/send_confirmation_email", method = RequestMethod.GET)
    public String sendConfirmationEmail(
            @RequestParam(value = "token") String token,
            Model model
    ) {
        String error = null;

        try {
            // Get email from token
            // entity = registrationConfirmationService.sendEmail(email);
        } catch (MailException ex) {
            error = "Invalid token.";
        }

        model.addAttribute("error", error);
        return "registrationConfirmation";
    }
}
