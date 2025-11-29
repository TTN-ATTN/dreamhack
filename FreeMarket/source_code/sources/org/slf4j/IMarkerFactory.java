package org.slf4j;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/slf4j-api-1.7.32.jar:org/slf4j/IMarkerFactory.class */
public interface IMarkerFactory {
    Marker getMarker(String str);

    boolean exists(String str);

    boolean detachMarker(String str);

    Marker getDetachedMarker(String str);
}
