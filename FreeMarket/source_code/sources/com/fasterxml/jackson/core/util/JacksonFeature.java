package com.fasterxml.jackson.core.util;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-core-2.13.5.jar:com/fasterxml/jackson/core/util/JacksonFeature.class */
public interface JacksonFeature {
    boolean enabledByDefault();

    int getMask();

    boolean enabledIn(int i);
}
