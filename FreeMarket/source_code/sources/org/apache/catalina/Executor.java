package org.apache.catalina;

import java.util.concurrent.TimeUnit;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/Executor.class */
public interface Executor extends java.util.concurrent.Executor, Lifecycle {
    String getName();

    @Deprecated
    void execute(Runnable runnable, long j, TimeUnit timeUnit);
}
