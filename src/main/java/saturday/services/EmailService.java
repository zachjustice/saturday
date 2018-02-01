package saturday.services;

public interface EmailService {
    void sendEmail(String subject, String to, String from, String body);
}
