package org.springframework.web.bind;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/MissingServletRequestParameterException.class */
public class MissingServletRequestParameterException extends MissingRequestValueException {
    private final String parameterName;
    private final String parameterType;

    public MissingServletRequestParameterException(String parameterName, String parameterType) {
        this(parameterName, parameterType, false);
    }

    public MissingServletRequestParameterException(String parameterName, String parameterType, boolean missingAfterConversion) {
        super("", missingAfterConversion);
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }

    @Override // org.springframework.web.util.NestedServletException, java.lang.Throwable
    public String getMessage() {
        return "Required request parameter '" + this.parameterName + "' for method parameter type " + this.parameterType + " is " + (isMissingAfterConversion() ? "present but converted to null" : "not present");
    }

    public final String getParameterName() {
        return this.parameterName;
    }

    public final String getParameterType() {
        return this.parameterType;
    }
}
