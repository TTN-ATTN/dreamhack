package org.springframework.mail;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/MailPreparationException.class */
public class MailPreparationException extends MailException {
    public MailPreparationException(String msg) {
        super(msg);
    }

    public MailPreparationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MailPreparationException(Throwable cause) {
        super("Could not prepare mail", cause);
    }
}
