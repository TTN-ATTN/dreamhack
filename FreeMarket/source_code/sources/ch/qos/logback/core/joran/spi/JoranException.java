package ch.qos.logback.core.joran.spi;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/joran/spi/JoranException.class */
public class JoranException extends Exception {
    private static final long serialVersionUID = 1112493363728774021L;

    public JoranException(String msg) {
        super(msg);
    }

    public JoranException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
