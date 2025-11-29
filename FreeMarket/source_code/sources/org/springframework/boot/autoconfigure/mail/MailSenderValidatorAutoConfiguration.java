package org.springframework.boot.autoconfigure.mail;

import javax.mail.MessagingException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@AutoConfiguration(after = {MailSenderAutoConfiguration.class})
@ConditionalOnProperty(prefix = "spring.mail", value = {"test-connection"})
@ConditionalOnSingleCandidate(JavaMailSenderImpl.class)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mail/MailSenderValidatorAutoConfiguration.class */
public class MailSenderValidatorAutoConfiguration {
    private final JavaMailSenderImpl mailSender;

    public MailSenderValidatorAutoConfiguration(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
        validateConnection();
    }

    public void validateConnection() {
        try {
            this.mailSender.testConnection();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Mail server is not available", ex);
        }
    }
}
