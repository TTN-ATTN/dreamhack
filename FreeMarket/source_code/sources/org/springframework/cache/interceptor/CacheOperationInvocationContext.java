package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.BasicOperation;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheOperationInvocationContext.class */
public interface CacheOperationInvocationContext<O extends BasicOperation> {
    O getOperation();

    Object getTarget();

    Method getMethod();

    Object[] getArgs();
}
