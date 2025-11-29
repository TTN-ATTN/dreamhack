package org.apache.logging.log4j;

import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/Marker.class */
public interface Marker extends Serializable {
    Marker addParents(Marker... markers);

    boolean equals(Object obj);

    String getName();

    Marker[] getParents();

    int hashCode();

    boolean hasParents();

    boolean isInstanceOf(Marker m);

    boolean isInstanceOf(String name);

    boolean remove(Marker marker);

    Marker setParents(Marker... markers);
}
