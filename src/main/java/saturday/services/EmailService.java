package saturday.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String subject, String to, String from, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setSubject(subject);
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
