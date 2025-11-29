package org.slf4j.event;

import org.slf4j.Marker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/slf4j-api-1.7.32.jar:org/slf4j/event/LoggingEvent.class */
public interface LoggingEvent {
    Level getLevel();

    Marker getMarker();

    String getLoggerName();

    String getMessage();

    String getThreadName();

    Object[] getArgumentArray();

    long getTimeStamp();

    Throwable getThrowable();
}
