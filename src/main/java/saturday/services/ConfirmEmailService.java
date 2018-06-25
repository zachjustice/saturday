package saturday.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import saturday.domain.accessTokens.AccessToken;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.utils.FileUtils;
import saturday.utils.TokenAuthenticationUtils;

import java.io.IOException;
import java.util.Base64;

@Service()
public class ConfirmEmailService {

    private final EmailService emailService;
    private final AccessTokenService accessTokenService;
    private final EntityService entityService;

    @Value("${saturday.client-url}")
    private String SATURDAY_CLIENT_URL;
    @Value("${saturday.access-token-type.email-confirmation}")
    private int ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION;
    @Value("${saturday.ses.from-email}")
    private String FROM_EMAIL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String CONFIRMATON_EMAIL_SUBJECT = "Activate Your Account";
    private static final String CONFIRMATON_URL_PLACEHOLDER = "{{CONFIRMATION_URL}}";

    @Autowired
    public ConfirmEmailService(EmailService emailService, AccessTokenService accessTokenService, EntityService entityService) {
        this.emailService = emailService;
        this.accessTokenService = accessTokenService;
        this.entityService = entityService;
    }

    /**
     * Sends an email asking the user to confirm their email address.
     *
     * @param email The email of the entity to whom we send the email
     * @throws MailException if there is a problem sending the email
     */
    public void sendEmail(String email) throws MailException {
        Entity entity = entityService.findEntityByEmail(email);
        sendEmail(entity);
    }

    /**
     * Sends an email asking the user to confirm their email address.
     *
     * @param entity The entity to whom we send the email
     * @throws MailException if there is a problem sending the email
     */
    public void sendEmail(Entity entity) throws MailException {
        AccessTokenType accessTokenType = new AccessTokenType();
        accessTokenType.setId(ACCESS_TOKEN_TYPE_EMAIL_CONFIRMATION);
        AccessToken accessToken = accessTokenService.save(entity, 60 * 60 * 24 * 1000, accessTokenType);

        String confirmationEmailTemplate;
        ClassPathResource cpr = new ClassPathResource("templates/confirm_email.html");
        try {
            confirmationEmailTemplate = FileUtils.classpathResourceToString(cpr);
        } catch (IOException e) {
            logger.error("Failed to send account confirmation email for entity " + entity, e);
            return;
        }

        String confirmationEmailBody = confirmationEmailTemplate.replace(
                CONFIRMATON_URL_PLACEHOLDER,
                constructVerificationUrl(accessToken.getToken(), entity.getEmail())
        );

        emailService.sendEmail(CONFIRMATON_EMAIL_SUBJECT, entity.getEmail(), FROM_EMAIL, confirmationEmailBody);
    }

    /**
     * Validate a token used for registration confirmation
     * Throws exception if the token is invalid
     *
     * @param token The token to validate
     * @return the Entity for the token if the token is valid
     * @throws ExpiredJwtException   If the token is expired
     * @throws MalformedJwtException If the token is malformed
     * @throws AccessDeniedException if the token is:
     *                               - if the token is not found in the db
     *                               - if the token is not a registration token
     */
    public Entity validateConfirmEmailToken(String token) {
        String email = TokenAuthenticationUtils.validateToken(token);

        // Check if the user has already confirmed their email address
        Entity entity = entityService.findEntityByEmail(email);
        if (entity.getIsEmailConfirmed()) {
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
        entity.setIsEmailConfirmed(true);
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
    private String constructVerificationUrl(String token, String email) {
        String base64token = Base64.getEncoder().encodeToString(token.getBytes());
        String base64email = Base64.getEncoder().encodeToString(email.getBytes());
        return SATURDAY_CLIENT_URL + "confirm_email?t=" + base64token + "&e=" + base64email;
    }
}
