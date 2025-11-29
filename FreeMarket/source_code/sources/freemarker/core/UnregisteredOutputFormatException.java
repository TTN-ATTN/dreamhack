package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnregisteredOutputFormatException.class */
public class UnregisteredOutputFormatException extends Exception {
    public UnregisteredOutputFormatException(String message) {
        this(message, null);
    }

    public UnregisteredOutputFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
