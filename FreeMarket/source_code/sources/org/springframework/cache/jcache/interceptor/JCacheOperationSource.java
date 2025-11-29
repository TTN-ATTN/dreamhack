package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/JCacheOperationSource.class */
public interface JCacheOperationSource {
    @Nullable
    JCacheOperation<?> getCacheOperation(Method method, @Nullable Class<?> targetClass);
}
