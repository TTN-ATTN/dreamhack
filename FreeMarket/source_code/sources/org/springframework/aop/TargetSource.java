package org.springframework.aop;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/TargetSource.class */
public interface TargetSource extends TargetClassAware {
    @Override // org.springframework.aop.TargetClassAware
    @Nullable
    Class<?> getTargetClass();

    boolean isStatic();

    @Nullable
    Object getTarget() throws Exception;

    void releaseTarget(Object target) throws Exception;
}
