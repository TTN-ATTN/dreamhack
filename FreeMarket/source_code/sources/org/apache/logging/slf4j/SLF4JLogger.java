package org.apache.logging.slf4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-to-slf4j-2.17.2.jar:org/apache/logging/slf4j/SLF4JLogger.class */
public class SLF4JLogger extends AbstractLogger {
    private static final long serialVersionUID = 1;
    private final Logger logger;
    private final LocationAwareLogger locationAwareLogger;

    public SLF4JLogger(final String name, final MessageFactory messageFactory, final Logger logger) {
        super(name, messageFactory);
        this.logger = logger;
        this.locationAwareLogger = logger instanceof LocationAwareLogger ? (LocationAwareLogger) logger : null;
    }

    public SLF4JLogger(final String name, final Logger logger) {
        super(name);
        this.logger = logger;
        this.locationAwareLogger = logger instanceof LocationAwareLogger ? (LocationAwareLogger) logger : null;
    }

    private int convertLevel(final Level level) {
        switch (level.getStandardLevel()) {
            case DEBUG:
                return 10;
            case TRACE:
                return 0;
            case INFO:
                return 20;
            case WARN:
                return 30;
            case ERROR:
                return 40;
            default:
                return 40;
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public Level getLevel() {
        if (this.logger.isTraceEnabled()) {
            return Level.TRACE;
        }
        if (this.logger.isDebugEnabled()) {
            return Level.DEBUG;
        }
        if (this.logger.isInfoEnabled()) {
            return Level.INFO;
        }
        if (this.logger.isWarnEnabled()) {
            return Level.WARN;
        }
        if (this.logger.isErrorEnabled()) {
            return Level.ERROR;
        }
        return Level.OFF;
    }

    public Logger getLogger() {
        return this.locationAwareLogger != null ? this.locationAwareLogger : this.logger;
    }

    private static Marker getMarker(final org.apache.logging.log4j.Marker marker) {
        if (marker == null) {
            return null;
        }
        return convertMarker(marker);
    }

    private static Marker convertMarker(final org.apache.logging.log4j.Marker marker) {
        Marker slf4jMarker = MarkerFactory.getMarker(marker.getName());
        org.apache.logging.log4j.Marker[] parents = marker.getParents();
        if (parents != null) {
            for (org.apache.logging.log4j.Marker parent : parents) {
                Marker slf4jParent = getMarker(parent);
                if (!slf4jMarker.contains(slf4jParent)) {
                    slf4jMarker.add(slf4jParent);
                }
            }
        }
        return slf4jMarker;
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final Message data, final Throwable t) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final CharSequence data, final Throwable t) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final Object data, final Throwable t) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String data) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String data, final Object... p1) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return isEnabledFor(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final org.apache.logging.log4j.Marker marker, final String data, final Throwable t) {
        return isEnabledFor(level, marker);
    }

    private boolean isEnabledFor(final Level level, final org.apache.logging.log4j.Marker marker) {
        Marker slf4jMarker = getMarker(marker);
        switch (level.getStandardLevel()) {
            case DEBUG:
                return this.logger.isDebugEnabled(slf4jMarker);
            case TRACE:
                return this.logger.isTraceEnabled(slf4jMarker);
            case INFO:
                return this.logger.isInfoEnabled(slf4jMarker);
            case WARN:
                return this.logger.isWarnEnabled(slf4jMarker);
            case ERROR:
                return this.logger.isErrorEnabled(slf4jMarker);
            default:
                return this.logger.isErrorEnabled(slf4jMarker);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logMessage(final String fqcn, final Level level, final org.apache.logging.log4j.Marker marker, final Message message, final Throwable t) {
        Marker slf4jMarker = getMarker(marker);
        String formattedMessage = message.getFormattedMessage();
        if (this.locationAwareLogger != null) {
            if (message instanceof LoggerNameAwareMessage) {
                ((LoggerNameAwareMessage) message).setLoggerName(getName());
            }
            this.locationAwareLogger.log(slf4jMarker, fqcn, convertLevel(level), formattedMessage, null, t);
            return;
        }
        switch (level.getStandardLevel()) {
            case DEBUG:
                this.logger.debug(slf4jMarker, formattedMessage, t);
                break;
            case TRACE:
                this.logger.trace(slf4jMarker, formattedMessage, t);
                break;
            case INFO:
                this.logger.info(slf4jMarker, formattedMessage, t);
                break;
            case WARN:
                this.logger.warn(slf4jMarker, formattedMessage, t);
                break;
            case ERROR:
                this.logger.error(slf4jMarker, formattedMessage, t);
                break;
            default:
                this.logger.error(slf4jMarker, formattedMessage, t);
                break;
        }
    }
}
