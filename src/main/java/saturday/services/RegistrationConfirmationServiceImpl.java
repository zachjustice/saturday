package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.utils.TokenAuthenticationUtils;

@Service("registrationConfirmationServiceImpl ")
public class RegistrationConfirmationServiceImpl implements RegistrationConfirmationService {

    private final EmailService emailService;
    private final SimpleMailMessage templateMessage;

    @Value("${saturday.application-url}")
    private String APPLICATION_URL;

    @Autowired
    public RegistrationConfirmationServiceImpl(EmailService emailService, SimpleMailMessage templateMessage) {
        this.emailService = emailService;
        this.templateMessage = templateMessage;
    }

    /**
     * Sends an email asking the user to confirm their email address.
     * @param entity The entity to whom we send the email
     */
    @Override
    public void sendEmail(Entity entity) {
        String recipientEmail = entity.getEmail();
        String token = TokenAuthenticationUtils.createToken(recipientEmail, 60 * 24);

        SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
        message.setTo(entity.getEmail());
        message.setText("Confirm your email address with this link " + constructVerificationUrl(token));

        emailService.sendEmail(message);
    }

    /**
     * Constructs the confirm account link given an access token
     * @param token A token for account confirmation
     * @return The account confirmation url
     */
    private String constructVerificationUrl(String token) {
        return APPLICATION_URL + "/account_confirmation?token=" + token;
    }
}
