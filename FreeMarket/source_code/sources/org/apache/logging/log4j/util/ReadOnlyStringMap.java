package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/ReadOnlyStringMap.class */
public interface ReadOnlyStringMap extends Serializable {
    Map<String, String> toMap();

    boolean containsKey(String key);

    <V> void forEach(final BiConsumer<String, ? super V> action);

    <V, S> void forEach(final TriConsumer<String, ? super V, S> action, final S state);

    <V> V getValue(final String key);

    boolean isEmpty();

    int size();
}
