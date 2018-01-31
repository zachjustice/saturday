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

@Service("resetPasswordService")
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final AccessTokenService accessTokenService;
    private final SimpleMailMessage templateMessage;
    private final MailSender mailSender;

    @Value("${saturday.access-token-type.reset-password}")
    private int ACCESS_TOKEN_TYPE_RESET_PASSWORD;
    @Value("${saturday.application-url}")
    private String APPLICATION_URL;

    @Autowired
    public ResetPasswordServiceImpl(AccessTokenService accessTokenService, SimpleMailMessage templateMessage, MailSender mailSender) {
        this.accessTokenService = accessTokenService;
        this.templateMessage = templateMessage;
        this.mailSender = mailSender;
    }

    /**
     * Send a reset password email
     * @param entity The entity to whom to the send the reset password email
     * @throws MailException If the email is unable to send
     */
    @Override
    public void sendEmail(Entity entity) throws MailException {
        AccessTokenType accessTokenType = new AccessTokenType();
        accessTokenType.setId(ACCESS_TOKEN_TYPE_RESET_PASSWORD);
        AccessToken accessToken = accessTokenService.save(entity.getEmail(), 60 * 60 * 24 * 1000, accessTokenType);

        SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
        message.setSubject("Reset your password");
        message.setTo(entity.getEmail());
        message.setText(
            "Click this link or paste it in your browser to reset your password: \n"
            + constructUrl(accessToken.getToken())
            + "\n"
            + "For security, this link is only valid for 24 hours."
        );

        mailSender.send(message);
    }


    /**
     * Constructs the reset password link given an access token
     * @param token A token to reset the password
     * @return The email confirmation url
     */
    private String constructUrl(String token) {
        return APPLICATION_URL + "/reset_password?token=" + token;
    }
}
