package org.springframework.scheduling.annotation;

import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/annotation/AsyncConfigurerSupport.class */
public class AsyncConfigurerSupport implements AsyncConfigurer {
    @Override // org.springframework.scheduling.annotation.AsyncConfigurer
    public Executor getAsyncExecutor() {
        return null;
    }

    @Override // org.springframework.scheduling.annotation.AsyncConfigurer
    @Nullable
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
