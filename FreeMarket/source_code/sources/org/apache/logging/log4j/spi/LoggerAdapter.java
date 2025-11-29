package org.apache.logging.log4j.spi;

import java.io.Closeable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/spi/LoggerAdapter.class */
public interface LoggerAdapter<L> extends Closeable {
    L getLogger(String name);
}
