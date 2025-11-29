package org.apache.logging.log4j.internal;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Supplier;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/internal/DefaultLogBuilder.class */
public class DefaultLogBuilder implements LogBuilder {
    private static Message EMPTY_MESSAGE = new SimpleMessage("");
    private static final String FQCN = DefaultLogBuilder.class.getName();
    private static final Logger LOGGER = StatusLogger.getLogger();
    private final Logger logger;
    private Level level;
    private Marker marker;
    private Throwable throwable;
    private StackTraceElement location;
    private volatile boolean inUse;
    private long threadId;

    public DefaultLogBuilder(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
        this.threadId = Thread.currentThread().getId();
        this.inUse = true;
    }

    public DefaultLogBuilder(Logger logger) {
        this.logger = logger;
        this.inUse = false;
        this.threadId = Thread.currentThread().getId();
    }

    public LogBuilder reset(Level level) {
        this.inUse = true;
        this.level = level;
        this.marker = null;
        this.throwable = null;
        this.location = null;
        return this;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withMarker(Marker marker) {
        this.marker = marker;
        return this;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withLocation() {
        this.location = StackLocatorUtil.getStackTraceElement(2);
        return this;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public LogBuilder withLocation(StackTraceElement location) {
        this.location = location;
        return this;
    }

    public boolean isInUse() {
        return this.inUse;
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(Message message) {
        if (isValid()) {
            logMessage(message);
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(CharSequence message) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object... params) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, params));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Supplier<?>... params) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, LambdaUtil.getAll(params)));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(Supplier<Message> messageSupplier) {
        if (isValid()) {
            logMessage(messageSupplier.get());
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(Object message) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        if (isValid()) {
            logMessage(this.logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override // org.apache.logging.log4j.LogBuilder
    public void log() {
        if (isValid()) {
            logMessage(EMPTY_MESSAGE);
        }
    }

    private void logMessage(Message message) {
        try {
            this.logger.logMessage(this.level, this.marker, FQCN, this.location, message, this.throwable);
        } finally {
            this.inUse = false;
        }
    }

    private boolean isValid() {
        if (!this.inUse) {
            LOGGER.warn("Attempt to reuse LogBuilder was ignored. {}", StackLocatorUtil.getCallerClass(2));
            return false;
        }
        if (this.threadId != Thread.currentThread().getId()) {
            LOGGER.warn("LogBuilder can only be used on the owning thread. {}", StackLocatorUtil.getCallerClass(2));
            return false;
        }
        return true;
    }
}
