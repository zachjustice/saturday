package saturday.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailSenderConfig {

    @Value("${spring.mail.username}")
    private String SPRING_MAIL_USERNAME;
    @Value("${spring.mail.password}")
    private String SPRING_MAIL_PASSWORD;
    @Value("${spring.mail.host}")
    private String SPRING_MAIL_HOST;
    @Value("${spring.mail.properties.mail.smtp.port}")
    private int SPRING_MAIL_SMTP_PORT;
    @Value("${saturday.ses.from-email}")
    private String FROM_EMAIL;

    @Bean
    public JavaMailSenderImpl getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(SPRING_MAIL_HOST);
        mailSender.setPort(SPRING_MAIL_SMTP_PORT);
        mailSender.setUsername(SPRING_MAIL_USERNAME);
        mailSender.setPassword(SPRING_MAIL_PASSWORD);

        return mailSender;
    }

    @Bean
    public SimpleMailMessage getSimpleMailMessage() {
        SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom(FROM_EMAIL);
        return templateMessage;
    }
}
