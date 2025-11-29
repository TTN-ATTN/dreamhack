package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/InvalidFormatParametersException.class */
public final class InvalidFormatParametersException extends InvalidFormatStringException {
    public InvalidFormatParametersException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormatParametersException(String message) {
        this(message, null);
    }
}
