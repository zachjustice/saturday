package saturday.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.utils.FileUtils;
import saturday.utils.TokenAuthenticationUtils;

import java.io.IOException;

@Service("registrationConfirmationServiceImpl ")
public class RegistrationConfirmationServiceImpl implements RegistrationConfirmationService {

    private final EmailService emailService;
    private final AccessTokenService accessTokenService;
    private final EntityService entityService;

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
    public RegistrationConfirmationServiceImpl(EmailService emailService, AccessTokenService accessTokenService, EntityService entityService) {
        this.emailService = emailService;
        this.accessTokenService = accessTokenService;
        this.entityService = entityService;
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
     * Validate a token used for registration confirmation
     * Throws exception if the token is invalid
     * @param token The token to validate
     * @return the Entity for the token if the token is valid
     * @throws ExpiredJwtException If the token is expired
     * @throws MalformedJwtException If the token is malformed
     * @throws AccessDeniedException if the token is:
     *   - if the token is not found in the db
     *   - if the token is not a registration token
     */
    @Override
    public Entity validateRegistrationConfirmationToken(String token) {
        String email = TokenAuthenticationUtils.validateToken(token);

        // Check if the user has already confirmed their email address
        Entity entity = entityService.findEntityByEmail(email);
        if(entity.isEmailConfirmed()) {
            return entity;
        }

        // query the access token table by token to make sure its still valid
        // (i.e. its not being reused)
        AccessToken existing;
        try {
            existing = accessTokenService.findByToken(token);
        } catch (ResourceNotFoundException e) {
            throw new AccessDeniedException("Access token is not valid.");
        }

        // Make sure they're using the correct kind of token
        // i.e. we don't want people to use a normal auth or password reset token to confirm the email
        if (existing.getType().getId() != ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION) {
            throw new AccessDeniedException("Access token is invalid.");
        }

        // update the user's status to reflect the confirmed email
        entity.setEmailConfirmed(true);
        entityService.updateEntity(entity);

        // delete the access token after we've updated the user
        accessTokenService.deleteAccessTokenByToken(existing.getToken());
        return entity;
    }

    /**
     * Constructs the confirmation email link given an access token
     *
     * @param token A token for email confirmation
     * @return The email confirmation url
     */
    private String constructVerificationUrl(String token) {
        return APPLICATION_URL + "/registration_confirmation?token=" + token;
    }
}
