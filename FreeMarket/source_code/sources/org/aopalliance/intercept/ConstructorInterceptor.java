package org.aopalliance.intercept;

import javax.annotation.Nonnull;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/aopalliance/intercept/ConstructorInterceptor.class */
public interface ConstructorInterceptor extends Interceptor {
    @Nonnull
    Object construct(ConstructorInvocation invocation) throws Throwable;
}
