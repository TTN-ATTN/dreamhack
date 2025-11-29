package org.springframework.boot.autoconfigure.neo4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.driver.Logger;
import org.neo4j.driver.Logging;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jSpringJclLogging.class */
class Neo4jSpringJclLogging implements Logging {
    private static final String AUTOMATIC_PREFIX = "org.neo4j.driver.";

    Neo4jSpringJclLogging() {
    }

    public Logger getLog(String name) {
        String requestedLog = name;
        if (!requestedLog.startsWith(AUTOMATIC_PREFIX)) {
            requestedLog = AUTOMATIC_PREFIX + name;
        }
        Log springJclLog = LogFactory.getLog(requestedLog);
        return new SpringJclLogger(springJclLog);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jSpringJclLogging$SpringJclLogger.class */
    private static final class SpringJclLogger implements Logger {
        private final Log delegate;

        SpringJclLogger(Log delegate) {
            this.delegate = delegate;
        }

        public void error(String message, Throwable cause) {
            this.delegate.error(message, cause);
        }

        public void info(String format, Object... params) {
            this.delegate.info(String.format(format, params));
        }

        public void warn(String format, Object... params) {
            this.delegate.warn(String.format(format, params));
        }

        public void warn(String message, Throwable cause) {
            this.delegate.warn(message, cause);
        }

        public void debug(String format, Object... params) {
            if (isDebugEnabled()) {
                this.delegate.debug(String.format(format, params));
            }
        }

        public void debug(String message, Throwable throwable) {
            if (isDebugEnabled()) {
                this.delegate.debug(message, throwable);
            }
        }

        public void trace(String format, Object... params) {
            if (isTraceEnabled()) {
                this.delegate.trace(String.format(format, params));
            }
        }

        public boolean isTraceEnabled() {
            return this.delegate.isTraceEnabled();
        }

        public boolean isDebugEnabled() {
            return this.delegate.isDebugEnabled();
        }
    }
}
