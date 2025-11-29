package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheErrorHandler.class */
public interface CacheErrorHandler {
    void handleCacheGetError(RuntimeException exception, Cache cache, Object key);

    void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value);

    void handleCacheEvictError(RuntimeException exception, Cache cache, Object key);

    void handleCacheClearError(RuntimeException exception, Cache cache);
}
