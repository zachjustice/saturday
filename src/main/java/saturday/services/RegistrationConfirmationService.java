package saturday.services;

import org.springframework.mail.MailException;
import saturday.domain.Entity;

public interface RegistrationConfirmationService {
    void sendEmail(String email) throws MailException;

    void sendEmail(Entity entity) throws MailException;

    Entity validateRegistrationConfirmationToken(String token);
}
