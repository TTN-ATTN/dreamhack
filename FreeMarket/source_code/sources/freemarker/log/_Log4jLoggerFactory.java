package freemarker.log;

import org.apache.log4j.Level;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/_Log4jLoggerFactory.class */
public class _Log4jLoggerFactory implements LoggerFactory {
    @Override // freemarker.log.LoggerFactory
    public Logger getLogger(String category) {
        return new Log4jLogger(org.apache.log4j.Logger.getLogger(category));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/_Log4jLoggerFactory$Log4jLogger.class */
    private static class Log4jLogger extends Logger {
        private final org.apache.log4j.Logger logger;

        Log4jLogger(org.apache.log4j.Logger logger) {
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
        public void error(String message) {
            this.logger.error(message);
        }

        @Override // freemarker.log.Logger
        public void error(String message, Throwable t) {
            this.logger.error(message, t);
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
        public boolean isDebugEnabled() {
            return this.logger.isDebugEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isInfoEnabled() {
            return this.logger.isInfoEnabled();
        }

        @Override // freemarker.log.Logger
        public boolean isWarnEnabled() {
            return this.logger.isEnabledFor(Level.WARN);
        }

        @Override // freemarker.log.Logger
        public boolean isErrorEnabled() {
            return this.logger.isEnabledFor(Level.ERROR);
        }

        @Override // freemarker.log.Logger
        public boolean isFatalEnabled() {
            return this.logger.isEnabledFor(Level.FATAL);
        }
    }
}
