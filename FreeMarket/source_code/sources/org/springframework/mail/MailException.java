package org.springframework.mail;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/MailException.class */
public abstract class MailException extends NestedRuntimeException {
    public MailException(String msg) {
        super(msg);
    }

    public MailException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
