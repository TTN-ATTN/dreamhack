package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-classic-1.2.12.jar:ch/qos/logback/classic/boolex/IEvaluator.class */
public interface IEvaluator {
    boolean doEvaluate(ILoggingEvent iLoggingEvent);
}
