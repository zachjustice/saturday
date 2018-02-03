package saturday.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
    public String greeting(
            @RequestParam(value="token") String token,
            Model model
    ) {
        Entity entity = null;
        String error = null;

        try {
            entity = registrationConfirmationService.validateRegistrationConfirmationToken(token);
        } catch (ExpiredJwtException ex) {
            error = "Oops, looks your token is too old!";
        } catch (MalformedJwtException ex) {
            error = "Oops, we couldn't confirm your email! " +
                    "Make sure to copy and paste the whole link into your browser.";
        } catch (AccessDeniedException ex) {
            error = "Oops, we couldn't confirm your email!";
        }

        model.addAttribute("entity", entity);
        model.addAttribute("error", error);
        return "registrationConfirmation";
    }
}
