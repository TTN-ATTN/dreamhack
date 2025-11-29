package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/Mergeable.class */
public interface Mergeable {
    boolean isMergeEnabled();

    Object merge(@Nullable Object obj);
}
