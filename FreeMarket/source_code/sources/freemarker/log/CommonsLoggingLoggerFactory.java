package freemarker.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/CommonsLoggingLoggerFactory.class */
public class CommonsLoggingLoggerFactory implements LoggerFactory {
    @Override // freemarker.log.LoggerFactory
    public Logger getLogger(String category) {
        return new CommonsLoggingLogger(LogFactory.getLog(category));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/CommonsLoggingLoggerFactory$CommonsLoggingLogger.class */
    private static class CommonsLoggingLogger extends Logger {
        private final Log logger;

        CommonsLoggingLogger(Log logger) {
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
            return this.logger.isFatalEnabled();
        }
    }
}
