package org.springframework.boot.logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/DeferredLog.class */
public class DeferredLog implements Log {
    private volatile Log destination;
    private final Supplier<Log> destinationSupplier;
    private final Lines lines;

    public DeferredLog() {
        this.destinationSupplier = null;
        this.lines = new Lines();
    }

    DeferredLog(Supplier<Log> destination, Lines lines) {
        Assert.notNull(destination, "Destination must not be null");
        this.destinationSupplier = destination;
        this.lines = lines;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isTraceEnabled() {
        boolean z;
        synchronized (this.lines) {
            z = this.destination == null || this.destination.isTraceEnabled();
        }
        return z;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isDebugEnabled() {
        boolean z;
        synchronized (this.lines) {
            z = this.destination == null || this.destination.isDebugEnabled();
        }
        return z;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isInfoEnabled() {
        boolean z;
        synchronized (this.lines) {
            z = this.destination == null || this.destination.isInfoEnabled();
        }
        return z;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isWarnEnabled() {
        boolean z;
        synchronized (this.lines) {
            z = this.destination == null || this.destination.isWarnEnabled();
        }
        return z;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isErrorEnabled() {
        boolean z;
        synchronized (this.lines) {
            z = this.destination == null || this.destination.isErrorEnabled();
        }
        return z;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isFatalEnabled() {
        boolean z;
        synchronized (this.lines) {
            z = this.destination == null || this.destination.isFatalEnabled();
        }
        return z;
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message) {
        log(LogLevel.TRACE, message, null);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message, Throwable t) {
        log(LogLevel.TRACE, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message) {
        log(LogLevel.DEBUG, message, null);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message, Throwable t) {
        log(LogLevel.DEBUG, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message) {
        log(LogLevel.INFO, message, null);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message, Throwable t) {
        log(LogLevel.INFO, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message) {
        log(LogLevel.WARN, message, null);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message, Throwable t) {
        log(LogLevel.WARN, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message) {
        log(LogLevel.ERROR, message, null);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message, Throwable t) {
        log(LogLevel.ERROR, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message) {
        log(LogLevel.FATAL, message, null);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message, Throwable t) {
        log(LogLevel.FATAL, message, t);
    }

    private void log(LogLevel level, Object message, Throwable t) {
        synchronized (this.lines) {
            if (this.destination != null) {
                logTo(this.destination, level, message, t);
            } else {
                this.lines.add(this.destinationSupplier, level, message, t);
            }
        }
    }

    void switchOver() {
        this.destination = this.destinationSupplier.get();
    }

    public void switchTo(Class<?> destination) {
        switchTo(LogFactory.getLog(destination));
    }

    public void switchTo(Log destination) {
        synchronized (this.lines) {
            replayTo(destination);
            this.destination = destination;
        }
    }

    public void replayTo(Class<?> destination) {
        replayTo(LogFactory.getLog(destination));
    }

    public void replayTo(Log destination) {
        synchronized (this.lines) {
            Iterator<Line> it = this.lines.iterator();
            while (it.hasNext()) {
                Line line = it.next();
                logTo(destination, line.getLevel(), line.getMessage(), line.getThrowable());
            }
            this.lines.clear();
        }
    }

    public static Log replay(Log source, Class<?> destination) {
        return replay(source, LogFactory.getLog(destination));
    }

    public static Log replay(Log source, Log destination) {
        if (source instanceof DeferredLog) {
            ((DeferredLog) source).replayTo(destination);
        }
        return destination;
    }

    static void logTo(Log log, LogLevel level, Object message, Throwable throwable) {
        switch (level) {
            case TRACE:
                log.trace(message, throwable);
                break;
            case DEBUG:
                log.debug(message, throwable);
                break;
            case INFO:
                log.info(message, throwable);
                break;
            case WARN:
                log.warn(message, throwable);
                break;
            case ERROR:
                log.error(message, throwable);
                break;
            case FATAL:
                log.fatal(message, throwable);
                break;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/DeferredLog$Lines.class */
    static class Lines implements Iterable<Line> {
        private final List<Line> lines = new ArrayList();

        Lines() {
        }

        void add(Supplier<Log> destinationSupplier, LogLevel level, Object message, Throwable throwable) {
            this.lines.add(new Line(destinationSupplier, level, message, throwable));
        }

        void clear() {
            this.lines.clear();
        }

        @Override // java.lang.Iterable
        public Iterator<Line> iterator() {
            return this.lines.iterator();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/DeferredLog$Line.class */
    static class Line {
        private final Supplier<Log> destinationSupplier;
        private final LogLevel level;
        private final Object message;
        private final Throwable throwable;

        Line(Supplier<Log> destinationSupplier, LogLevel level, Object message, Throwable throwable) {
            this.destinationSupplier = destinationSupplier;
            this.level = level;
            this.message = message;
            this.throwable = throwable;
        }

        Log getDestination() {
            return this.destinationSupplier.get();
        }

        LogLevel getLevel() {
            return this.level;
        }

        Object getMessage() {
            return this.message;
        }

        Throwable getThrowable() {
            return this.throwable;
        }
    }
}
