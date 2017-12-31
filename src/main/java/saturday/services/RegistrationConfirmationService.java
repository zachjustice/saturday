package saturday.services;

import saturday.domain.Entity;

import javax.mail.MessagingException;

public interface RegistrationConfirmationService {
    void sendEmail(Entity entity) throws MessagingException;
}
