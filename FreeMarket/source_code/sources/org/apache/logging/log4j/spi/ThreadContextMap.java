package org.apache.logging.log4j.spi;

import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/spi/ThreadContextMap.class */
public interface ThreadContextMap {
    void clear();

    boolean containsKey(final String key);

    String get(final String key);

    Map<String, String> getCopy();

    Map<String, String> getImmutableMapOrNull();

    boolean isEmpty();

    void put(final String key, final String value);

    void remove(final String key);
}
