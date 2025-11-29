package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/annotation/CachingConfigurer.class */
public interface CachingConfigurer {
    @Nullable
    default CacheManager cacheManager() {
        return null;
    }

    @Nullable
    default CacheResolver cacheResolver() {
        return null;
    }

    @Nullable
    default KeyGenerator keyGenerator() {
        return null;
    }

    @Nullable
    default CacheErrorHandler errorHandler() {
        return null;
    }
}
