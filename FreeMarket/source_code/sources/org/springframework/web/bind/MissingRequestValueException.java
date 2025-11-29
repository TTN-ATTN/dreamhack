package org.springframework.web.bind;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/MissingRequestValueException.class */
public class MissingRequestValueException extends ServletRequestBindingException {
    private final boolean missingAfterConversion;

    public MissingRequestValueException(String msg) {
        this(msg, false);
    }

    public MissingRequestValueException(String msg, boolean missingAfterConversion) {
        super(msg);
        this.missingAfterConversion = missingAfterConversion;
    }

    public boolean isMissingAfterConversion() {
        return this.missingAfterConversion;
    }
}
