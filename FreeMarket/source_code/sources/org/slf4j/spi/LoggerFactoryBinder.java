package org.slf4j.spi;

import org.slf4j.ILoggerFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/slf4j-api-1.7.32.jar:org/slf4j/spi/LoggerFactoryBinder.class */
public interface LoggerFactoryBinder {
    ILoggerFactory getLoggerFactory();

    String getLoggerFactoryClassStr();
}
