package saturday.services;

import org.springframework.mail.MailException;
import saturday.domain.Entity;

public interface ConfirmEmailService {
    void sendEmail(String email) throws MailException;

    void sendEmail(Entity entity) throws MailException;

    Entity validateConfirmEmailToken(String token);
}
