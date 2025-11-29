package org.springframework.util.concurrent;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/concurrent/FailureCallback.class */
public interface FailureCallback {
    void onFailure(Throwable ex);
}
