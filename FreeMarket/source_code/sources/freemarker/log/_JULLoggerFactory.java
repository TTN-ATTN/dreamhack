package freemarker.log;

import java.util.logging.Level;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/_JULLoggerFactory.class */
public class _JULLoggerFactory implements LoggerFactory {
    @Override // freemarker.log.LoggerFactory
    public Logger getLogger(String category) {
        return new JULLogger(java.util.logging.Logger.getLogger(category));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/_JULLoggerFactory$JULLogger.class */
    private static class JULLogger extends Logger {
        private final java.util.logging.Logger logger;

        JULLogger(java.util.logging.Logger logger) {
            this.logger = logger;
        }

        @Override // freemarker.log.Logger
        public void debug(String message) {
            this.logger.log(Level.FINE, message);
        }

        @Override // freemarker.log.Logger
        public void debug(String message, Throwable t) {
            this.logger.log(Level.FINE, message, t);
        }

        @Override // freemarker.log.Logger
        public void error(String message) {
            this.logger.log(Level.SEVERE, message);
        }

        @Override // freemarker.log.Logger
        public void error(String message, Throwable t) {
            this.logger.log(Level.SEVERE, message, t);
        }

        @Override // freemarker.log.Logger
        public void info(String message) {
            this.logger.log(Level.INFO, message);
        }

        @Override // freemarker.log.Logger
        public void info(String message, Throwable t) {
            this.logger.log(Level.INFO, message, t);
        }

        @Override // freemarker.log.Logger
        public void warn(String message) {
            this.logger.log(Level.WARNING, message);
        }

        @Override // freemarker.log.Logger
        public void warn(String message, Throwable t) {
            this.logger.log(Level.WARNING, message, t);
        }

        @Override // freemarker.log.Logger
        public boolean isDebugEnabled() {
            return this.logger.isLoggable(Level.FINE);
        }

        @Override // freemarker.log.Logger
        public boolean isInfoEnabled() {
            return this.logger.isLoggable(Level.INFO);
        }

        @Override // freemarker.log.Logger
        public boolean isWarnEnabled() {
            return this.logger.isLoggable(Level.WARNING);
        }

        @Override // freemarker.log.Logger
        public boolean isErrorEnabled() {
            return this.logger.isLoggable(Level.SEVERE);
        }

        @Override // freemarker.log.Logger
        public boolean isFatalEnabled() {
            return this.logger.isLoggable(Level.SEVERE);
        }
    }
}
