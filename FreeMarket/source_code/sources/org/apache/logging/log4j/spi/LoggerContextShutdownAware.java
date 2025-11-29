package org.apache.logging.log4j.spi;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/spi/LoggerContextShutdownAware.class */
public interface LoggerContextShutdownAware {
    void contextShutdown(LoggerContext loggerContext);
}
