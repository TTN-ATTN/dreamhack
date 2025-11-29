package ch.qos.logback.core.spi;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/spi/ScanException.class */
public class ScanException extends Exception {
    private static final long serialVersionUID = -3132040414328475658L;
    Throwable cause;

    public ScanException(String msg) {
        super(msg);
    }

    public ScanException(String msg, Throwable rootCause) {
        super(msg);
        this.cause = rootCause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
