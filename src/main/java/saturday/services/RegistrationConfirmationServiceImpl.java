package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import saturday.domain.AccessToken;
import saturday.domain.AccessTokenType;
import saturday.domain.Entity;
import saturday.utils.FileUtils;

import java.io.IOException;

@Service("registrationConfirmationServiceImpl ")
public class RegistrationConfirmationServiceImpl implements RegistrationConfirmationService {

    private final EmailService emailService;
    private final AccessTokenService accessTokenService;

    @Value("${saturday.application-url}")
    private String APPLICATION_URL;
    @Value("${saturday.access-token-type.email-confirmation}")
    private int ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION;
    @Value("classpath:templates/confirm_email.html")
    private Resource confirmationEmailResource;
    @Value("${saturday.ses.from-email}")
    private String FROM_EMAIL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String CONFIRMATON_EMAIL_SUBJECT = "Activate Your Account";
    private static final String CONFIRMATON_URL_PLACEHOLDER = "{{CONFIRMATION_URL}}";

    @Autowired
    public RegistrationConfirmationServiceImpl(EmailService emailService, AccessTokenService accessTokenService) {
        this.emailService = emailService;
        this.accessTokenService = accessTokenService;
    }

    /**
     * Sends an email asking the user to confirm their email address.
     *
     * @param entity The entity to whom we send the email
     * @throws MailException if there is a problem sending the email
     */
    @Override
    public void sendEmail(Entity entity) throws MailException {
        AccessTokenType accessTokenType = new AccessTokenType();
        accessTokenType.setId(ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION);
        AccessToken accessToken = accessTokenService.save(entity.getEmail(), 60 * 60 * 24 * 1000, accessTokenType);

        StringBuilder confirmationEmailTemplate;
        try {
            confirmationEmailTemplate = FileUtils.toStringBuilder(confirmationEmailResource);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String confirmationEmailBody = confirmationEmailTemplate.toString().replace(
                CONFIRMATON_URL_PLACEHOLDER,
                constructVerificationUrl(accessToken.getToken())
        );

        emailService.sendEmail(CONFIRMATON_EMAIL_SUBJECT, entity.getEmail(), FROM_EMAIL, confirmationEmailBody);
    }

    /**
     * Constructs the confirmation email link given an access token
     *
     * @param token A token for email confirmation
     * @return The email confirmation url
     */
    private String constructVerificationUrl(String token) {
        return APPLICATION_URL + "/email_confirmation?token=" + token;
    }
}
