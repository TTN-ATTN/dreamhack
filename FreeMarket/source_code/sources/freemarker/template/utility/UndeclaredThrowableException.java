package freemarker.template.utility;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/UndeclaredThrowableException.class */
public class UndeclaredThrowableException extends RuntimeException {
    public UndeclaredThrowableException(Throwable t) {
        super(t);
    }

    public UndeclaredThrowableException(String message, Throwable t) {
        super(message, t);
    }

    public Throwable getUndeclaredThrowable() {
        return getCause();
    }
}
