package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import saturday.domain.AccessToken;
import saturday.domain.AccessTokenType;
import saturday.domain.Entity;
import saturday.utils.TokenAuthenticationUtils;

import java.util.Date;

@Service("registrationConfirmationServiceImpl ")
public class RegistrationConfirmationServiceImpl implements RegistrationConfirmationService {

    private final MailSender mailSender;
    private final SimpleMailMessage templateMessage;
    private final AccessTokenService accessTokenService;

    @Value("${saturday.application-url}")
    private String APPLICATION_URL;
    @Value("${saturday.access-token-type.email-confirmation}")
    private int ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION;

    @Autowired
    public RegistrationConfirmationServiceImpl(MailSender mailSender, SimpleMailMessage templateMessage, AccessTokenService accessTokenService) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
        this.accessTokenService = accessTokenService;
    }

    /**
     * Sends an email asking the user to confirm their email address.
     * @param entity The entity to whom we send the email
     * @throws MailException if there is a problem sending the email
     */
    @Override
    public void sendEmail(Entity entity) throws MailException {
        String recipientEmail = entity.getEmail();
        Date expirationDate = new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000);
        String token = TokenAuthenticationUtils.createToken(recipientEmail, expirationDate);

        SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
        message.setSubject("Confirm your email address");
        //message.setTo(entity.getEmail());
        message.setTo("success@simulator.amazonses.com");
        message.setText("Confirm your email address with this link " + constructVerificationUrl(token));

        mailSender.send(message);
        saveToken(token, expirationDate);
    }

    private void saveToken(String token, Date expirationDate) {
        AccessTokenType accessTokenType = new AccessTokenType();
        accessTokenType.setId(ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION);

        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setExpirationDate(expirationDate);
        accessToken.setType(accessTokenType);

        accessTokenService.save(accessToken);
    }

    /**
     * Constructs the confirm email link given an access token
     * @param token A token for email confirmation
     * @return The email confirmation url
     */
    private String constructVerificationUrl(String token) {
        return APPLICATION_URL + "/email_confirmation?token=" + token;
    }
}
