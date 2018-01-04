package saturday.services;

import org.springframework.mail.MailException;
import saturday.domain.Entity;

public interface ResetPasswordService {
    void sendEmail(Entity entity) throws MailException;
}
