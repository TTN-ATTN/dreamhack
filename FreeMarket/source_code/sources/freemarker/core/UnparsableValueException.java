package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnparsableValueException.class */
public class UnparsableValueException extends TemplateValueFormatException {
    public UnparsableValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnparsableValueException(String message) {
        this(message, null);
    }
}
