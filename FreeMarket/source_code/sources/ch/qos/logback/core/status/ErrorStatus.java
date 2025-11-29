package ch.qos.logback.core.status;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/status/ErrorStatus.class */
public class ErrorStatus extends StatusBase {
    public ErrorStatus(String msg, Object origin) {
        super(2, msg, origin);
    }

    public ErrorStatus(String msg, Object origin, Throwable t) {
        super(2, msg, origin, t);
    }
}
