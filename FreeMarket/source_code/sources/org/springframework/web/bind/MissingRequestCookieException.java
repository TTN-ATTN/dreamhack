package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/MissingRequestCookieException.class */
public class MissingRequestCookieException extends MissingRequestValueException {
    private final String cookieName;
    private final MethodParameter parameter;

    public MissingRequestCookieException(String cookieName, MethodParameter parameter) {
        this(cookieName, parameter, false);
    }

    public MissingRequestCookieException(String cookieName, MethodParameter parameter, boolean missingAfterConversion) {
        super("", missingAfterConversion);
        this.cookieName = cookieName;
        this.parameter = parameter;
    }

    @Override // org.springframework.web.util.NestedServletException, java.lang.Throwable
    public String getMessage() {
        return "Required cookie '" + this.cookieName + "' for method parameter type " + this.parameter.getNestedParameterType().getSimpleName() + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getCookieName() {
        return this.cookieName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}
