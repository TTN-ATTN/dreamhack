package org.springframework.mail;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/MailAuthenticationException.class */
public class MailAuthenticationException extends MailException {
    public MailAuthenticationException(String msg) {
        super(msg);
    }

    public MailAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MailAuthenticationException(Throwable cause) {
        super("Authentication failed", cause);
    }
}
