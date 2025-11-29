package freemarker.log;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/_NullLoggerFactory.class */
public class _NullLoggerFactory implements LoggerFactory {
    private static final Logger INSTANCE = new Logger() { // from class: freemarker.log._NullLoggerFactory.1
        @Override // freemarker.log.Logger
        public void debug(String message) {
        }

        @Override // freemarker.log.Logger
        public void debug(String message, Throwable t) {
        }

        @Override // freemarker.log.Logger
        public void error(String message) {
        }

        @Override // freemarker.log.Logger
        public void error(String message, Throwable t) {
        }

        @Override // freemarker.log.Logger
        public void info(String message) {
        }

        @Override // freemarker.log.Logger
        public void info(String message, Throwable t) {
        }

        @Override // freemarker.log.Logger
        public void warn(String message) {
        }

        @Override // freemarker.log.Logger
        public void warn(String message, Throwable t) {
        }

        @Override // freemarker.log.Logger
        public boolean isDebugEnabled() {
            return false;
        }

        @Override // freemarker.log.Logger
        public boolean isInfoEnabled() {
            return false;
        }

        @Override // freemarker.log.Logger
        public boolean isWarnEnabled() {
            return false;
        }

        @Override // freemarker.log.Logger
        public boolean isErrorEnabled() {
            return false;
        }

        @Override // freemarker.log.Logger
        public boolean isFatalEnabled() {
            return false;
        }
    };

    _NullLoggerFactory() {
    }

    @Override // freemarker.log.LoggerFactory
    public Logger getLogger(String category) {
        return INSTANCE;
    }
}
