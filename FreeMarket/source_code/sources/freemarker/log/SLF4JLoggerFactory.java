package freemarker.log;

import org.slf4j.spi.LocationAwareLogger;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/SLF4JLoggerFactory.class */
public class SLF4JLoggerFactory implements LoggerFactory {
    @Override // freemarker.log.LoggerFactory
    public Logger getLogger(String category) {
        org.slf4j.Logger slf4jLogger = org.slf4j.LoggerFactory.getLogger(category);
        if (slf4jLogger instanceof LocationAwareLogger) {
            return new LocationAwareSLF4JLogger((LocationAwareLogger) slf4jLogger);
        }
        return new LocationUnawareSLF4JLogger(slf4jLogger);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/SLF4JLoggerFactory$LocationAwareSLF4JLogger.class */
    private static final class LocationAwareSLF4JLogger extends Logger {
        private static final String ADAPTER_FQCN = LocationAwareSLF4JLogger.class.getName();
        private final LocationAwareLogger logger;

        LocationAwareSLF4JLogger(LocationAwareLogger logger) {
            this.logger = logger;
        }

        @Override // freemarker.log.Logger
        public void debug(String message) {
            debug(message, null);
        }

        @Override // freemarker.log.Logger
        public void debug(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 10, message, null, t);
        }

        @Override // freemarker.log.Logger
        public void info(String message) {
            info(message, null);
        }

        @Override // freemarker.log.Logger
        public void info(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 20, message, null, t);
        }

        @Override // freemarker.log.Logger
        public void warn(String message) {
            warn(message, null);
        }

        @Override // freemarker.log.Logger
        public void warn(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 30, message, null, t);
        }

        @Override // freemarker.log.Logger
        public void error(String message) {
            error(message, null);
        }

        @Override // freemarker.log.Logger
        public void error(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 40, message, null, t);
        }

        @Override // freemarker.log.Logger
        public boolean isDebugEnabled() {
            return this.logger.isDebugEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isInfoEnabled() {
            return this.logger.isInfoEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isWarnEnabled() {
            return this.logger.isWarnEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isErrorEnabled() {
            return this.logger.isErrorEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isFatalEnabled() {
            return this.logger.isErrorEnabled();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/SLF4JLoggerFactory$LocationUnawareSLF4JLogger.class */
    private static class LocationUnawareSLF4JLogger extends Logger {
        private final org.slf4j.Logger logger;

        LocationUnawareSLF4JLogger(org.slf4j.Logger logger) {
            this.logger = logger;
        }

        @Override // freemarker.log.Logger
        public void debug(String message) {
            this.logger.debug(message);
        }

        @Override // freemarker.log.Logger
        public void debug(String message, Throwable t) {
            this.logger.debug(message, t);
        }

        @Override // freemarker.log.Logger
        public void info(String message) {
            this.logger.info(message);
        }

        @Override // freemarker.log.Logger
        public void info(String message, Throwable t) {
            this.logger.info(message, t);
        }

        @Override // freemarker.log.Logger
        public void warn(String message) {
            this.logger.warn(message);
        }

        @Override // freemarker.log.Logger
        public void warn(String message, Throwable t) {
            this.logger.warn(message, t);
        }

        @Override // freemarker.log.Logger
        public void error(String message) {
            this.logger.error(message);
        }

        @Override // freemarker.log.Logger
        public void error(String message, Throwable t) {
            this.logger.error(message, t);
        }

        @Override // freemarker.log.Logger
        public boolean isDebugEnabled() {
            return this.logger.isDebugEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isInfoEnabled() {
            return this.logger.isInfoEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isWarnEnabled() {
            return this.logger.isWarnEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isErrorEnabled() {
            return this.logger.isErrorEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isFatalEnabled() {
            return this.logger.isErrorEnabled();
        }
    }
}
