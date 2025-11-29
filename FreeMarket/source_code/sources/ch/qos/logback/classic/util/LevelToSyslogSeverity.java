package ch.qos.logback.classic.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-classic-1.2.12.jar:ch/qos/logback/classic/util/LevelToSyslogSeverity.class */
public class LevelToSyslogSeverity {
    public static int convert(ILoggingEvent event) {
        Level level = event.getLevel();
        switch (level.levelInt) {
            case Level.TRACE_INT /* 5000 */:
            case 10000:
                return 7;
            case 20000:
                return 6;
            case 30000:
                return 4;
            case Level.ERROR_INT /* 40000 */:
                return 3;
            default:
                throw new IllegalArgumentException("Level " + level + " is not a valid level for a printing method");
        }
    }
}
