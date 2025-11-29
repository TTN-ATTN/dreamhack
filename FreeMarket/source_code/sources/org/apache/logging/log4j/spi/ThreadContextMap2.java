package org.apache.logging.log4j.spi;

import java.util.Map;
import org.apache.logging.log4j.util.StringMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/spi/ThreadContextMap2.class */
public interface ThreadContextMap2 extends ThreadContextMap {
    void putAll(final Map<String, String> map);

    StringMap getReadOnlyContextData();
}
