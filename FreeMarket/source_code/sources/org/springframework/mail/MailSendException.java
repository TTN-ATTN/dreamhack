package org.springframework.mail;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/MailSendException.class */
public class MailSendException extends MailException {
    private final transient Map<Object, Exception> failedMessages;

    @Nullable
    private final Exception[] messageExceptions;

    public MailSendException(String msg) {
        this(msg, null);
    }

    public MailSendException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.failedMessages = new LinkedHashMap();
        this.messageExceptions = null;
    }

    public MailSendException(@Nullable String msg, @Nullable Throwable cause, Map<Object, Exception> failedMessages) {
        super(msg, cause);
        this.failedMessages = new LinkedHashMap(failedMessages);
        this.messageExceptions = (Exception[]) failedMessages.values().toArray(new Exception[0]);
    }

    public MailSendException(Map<Object, Exception> failedMessages) {
        this(null, null, failedMessages);
    }

    public final Map<Object, Exception> getFailedMessages() {
        return this.failedMessages;
    }

    public final Exception[] getMessageExceptions() {
        return this.messageExceptions != null ? this.messageExceptions : new Exception[0];
    }

    @Override // org.springframework.core.NestedRuntimeException, java.lang.Throwable
    @Nullable
    public String getMessage() {
        if (ObjectUtils.isEmpty((Object[]) this.messageExceptions)) {
            return super.getMessage();
        }
        StringBuilder sb = new StringBuilder();
        String baseMessage = super.getMessage();
        if (baseMessage != null) {
            sb.append(baseMessage).append(". ");
        }
        sb.append("Failed messages: ");
        for (int i = 0; i < this.messageExceptions.length; i++) {
            Exception subEx = this.messageExceptions[i];
            sb.append(subEx.toString());
            if (i < this.messageExceptions.length - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }

    @Override // java.lang.Throwable
    public String toString() {
        if (ObjectUtils.isEmpty((Object[]) this.messageExceptions)) {
            return super.toString();
        }
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("; message exceptions (").append(this.messageExceptions.length).append(") are:");
        for (int i = 0; i < this.messageExceptions.length; i++) {
            Exception subEx = this.messageExceptions[i];
            sb.append('\n').append("Failed message ").append(i + 1).append(": ");
            sb.append(subEx);
        }
        return sb.toString();
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream ps) {
        if (ObjectUtils.isEmpty((Object[]) this.messageExceptions)) {
            super.printStackTrace(ps);
            return;
        }
        ps.println(super.toString() + "; message exception details (" + this.messageExceptions.length + ") are:");
        for (int i = 0; i < this.messageExceptions.length; i++) {
            Exception subEx = this.messageExceptions[i];
            ps.println("Failed message " + (i + 1) + ":");
            subEx.printStackTrace(ps);
        }
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter pw) {
        if (ObjectUtils.isEmpty((Object[]) this.messageExceptions)) {
            super.printStackTrace(pw);
            return;
        }
        pw.println(super.toString() + "; message exception details (" + this.messageExceptions.length + ") are:");
        for (int i = 0; i < this.messageExceptions.length; i++) {
            Exception subEx = this.messageExceptions[i];
            pw.println("Failed message " + (i + 1) + ":");
            subEx.printStackTrace(pw);
        }
    }
}
