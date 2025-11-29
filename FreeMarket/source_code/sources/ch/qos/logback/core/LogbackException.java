package ch.qos.logback.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/LogbackException.class */
public class LogbackException extends RuntimeException {
    private static final long serialVersionUID = -799956346239073266L;

    public LogbackException(String msg) {
        super(msg);
    }

    public LogbackException(String msg, Throwable nested) {
        super(msg, nested);
    }
}
