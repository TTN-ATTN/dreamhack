package org.apache.logging.log4j.util;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/TriConsumer.class */
public interface TriConsumer<K, V, S> {
    void accept(K k, V v, S s);
}
