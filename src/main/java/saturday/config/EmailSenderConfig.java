package saturday.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailSenderConfig {

    @Value("${saturday.ses.smpt-user}")
    private String AWS_SES_SMPT_USER;
    @Value("${saturday.ses.smpt-password}")
    private String AWS_SES_SMPT_PASSWORD;
    @Value("${saturday.ses.smpt-server}")
    private String AWS_SES_SMPT_SERVER;
    @Value("${saturday.ses.smpt-port}")
    private int AWS_SES_SMPT_PORT;
    @Value("${saturday.ses.from-email}")
    private String AWS_SES_FROM_EMAIL;

    @Bean
    public JavaMailSenderImpl getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(AWS_SES_SMPT_SERVER);
        mailSender.setPort(AWS_SES_SMPT_PORT);
        mailSender.setUsername(AWS_SES_SMPT_USER);
        mailSender.setPassword(AWS_SES_SMPT_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", AWS_SES_SMPT_PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public SimpleMailMessage getSimpleMailMessage() {
        SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom(AWS_SES_FROM_EMAIL);
        return templateMessage;
    }
}
