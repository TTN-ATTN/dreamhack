package org.springframework.cache.jcache.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/config/JCacheConfigurer.class */
public interface JCacheConfigurer extends CachingConfigurer {
    @Nullable
    default CacheResolver exceptionCacheResolver() {
        return null;
    }
}
