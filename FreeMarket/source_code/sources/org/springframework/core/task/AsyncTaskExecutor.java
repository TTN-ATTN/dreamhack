package org.springframework.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/task/AsyncTaskExecutor.class */
public interface AsyncTaskExecutor extends TaskExecutor {

    @Deprecated
    public static final long TIMEOUT_IMMEDIATE = 0;

    @Deprecated
    public static final long TIMEOUT_INDEFINITE = Long.MAX_VALUE;

    @Deprecated
    void execute(Runnable task, long startTimeout);

    Future<?> submit(Runnable task);

    <T> Future<T> submit(Callable<T> task);
}
