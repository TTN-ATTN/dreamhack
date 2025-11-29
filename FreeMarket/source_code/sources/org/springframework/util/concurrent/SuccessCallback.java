package org.springframework.util.concurrent;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/concurrent/SuccessCallback.class */
public interface SuccessCallback<T> {
    void onSuccess(@Nullable T result);
}
