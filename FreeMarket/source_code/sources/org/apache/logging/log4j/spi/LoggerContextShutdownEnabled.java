package org.apache.logging.log4j.spi;

import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/spi/LoggerContextShutdownEnabled.class */
public interface LoggerContextShutdownEnabled {
    void addShutdownListener(LoggerContextShutdownAware listener);

    List<LoggerContextShutdownAware> getListeners();
}
