package saturday.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.services.RegistrationConfirmationService;

@Controller
public class RegistrationConfirmationController {

    private final RegistrationConfirmationService registrationConfirmationService;

    public RegistrationConfirmationController(RegistrationConfirmationService registrationConfirmationService) {
        this.registrationConfirmationService = registrationConfirmationService;
    }

    @RequestMapping(value = "/registration_confirmation", method = RequestMethod.GET)
    public String registrationConfirmation(
            @RequestParam(value = "token") String token,
            Model model
    ) {
        Entity entity = null;
        String error = null;

        try {
            entity = registrationConfirmationService.validateRegistrationConfirmationToken(token);
        } catch (ExpiredJwtException ex) {
            error = "Confirmation emails are only valid for 24 hours.";
        } catch (MalformedJwtException ex) {
            error = "Make sure to click 'Confirm your Email' or copy and paste the whole link into your browser.";
        } catch (AccessDeniedException ex) {
            error = "Invalid token.";
        }

        model.addAttribute("entity", entity);
        model.addAttribute("error", error);
        return "registrationConfirmation";
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
