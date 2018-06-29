package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import saturday.delegates.AccessTokenDelegate;
import saturday.domain.accessTokens.AccessToken;
import saturday.domain.accessTokenTypes.AccessTokenType;
import saturday.domain.Entity;
import saturday.utils.FileUtils;

import java.io.IOException;

@Service()
public class ResetPasswordService {

    private final AccessTokenDelegate accessTokenDelegate;
    private final EmailService emailService;

    @Value("${saturday.access-token-type.reset-password}")
    private int ACCESS_TOKEN_TYPE_RESET_PASSWORD;
    @Value("${saturday.client-url}")
    private String SATURDAY_CLIENT_URL;
    @Value("${saturday.ses.from-email}")
    private String FROM_EMAIL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ResetPasswordService(
            AccessTokenDelegate accessTokenDelegate,
            EmailService emailService
    ) {
        this.accessTokenDelegate = accessTokenDelegate;
        this.emailService = emailService;
    }

    /**
     * Send a reset password email
     * @param entity The entity to whom to the send the reset password email
     * @throws MailException If the email is unable to send
     */
    public void sendEmail(Entity entity) throws MailException {
        String token = accessTokenDelegate.saveResetPasswordToken(
                entity,
                60 * 60 * 24 * 1000
        );

        String forgotPasswordEmailTemplate;
        try {
            ClassPathResource cpr = new ClassPathResource("templates/forgot_password.html");
            forgotPasswordEmailTemplate = FileUtils.classpathResourceToString(cpr);
        } catch (IOException e) {
            logger.error("Failed to send reset password email for entity " + entity, e);
            return;
        }

        String forgotPasswordEmailBody = forgotPasswordEmailTemplate
                .replace(
                        "{{NAME}}",
                        entity.getName()
                ).replace(
                        "{{FORGOT_PASSWORD_CODE}}",
                        formatResetPasswordToken(token)
                );

        emailService.sendEmail("MomDiary Account Recovery", entity.getEmail(), FROM_EMAIL, forgotPasswordEmailBody);
    }

    private String formatResetPasswordToken(String token) {
        String[] tokenBrokenIntoThrees = token.split("(?<=\\G...)");
        return String.join("-", tokenBrokenIntoThrees);
    }
}
