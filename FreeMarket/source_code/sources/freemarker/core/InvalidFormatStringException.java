package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/InvalidFormatStringException.class */
public abstract class InvalidFormatStringException extends TemplateValueFormatException {
    public InvalidFormatStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormatStringException(String message) {
        this(message, null);
    }
}
