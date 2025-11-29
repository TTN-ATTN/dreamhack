package org.apache.coyote;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/AsyncContextCallback.class */
public interface AsyncContextCallback {
    void fireOnComplete();

    boolean isAvailable();

    void incrementInProgressAsyncCount();

    void decrementInProgressAsyncCount();
}
