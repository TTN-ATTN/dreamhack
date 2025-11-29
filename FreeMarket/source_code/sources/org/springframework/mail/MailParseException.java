package org.springframework.mail;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/MailParseException.class */
public class MailParseException extends MailException {
    public MailParseException(String msg) {
        super(msg);
    }

    public MailParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MailParseException(Throwable cause) {
        super("Could not parse mail", cause);
    }
}
