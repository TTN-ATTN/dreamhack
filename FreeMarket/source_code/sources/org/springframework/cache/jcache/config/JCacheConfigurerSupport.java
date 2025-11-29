package org.springframework.cache.jcache.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/config/JCacheConfigurerSupport.class */
public class JCacheConfigurerSupport extends CachingConfigurerSupport implements JCacheConfigurer {
    @Override // org.springframework.cache.jcache.config.JCacheConfigurer
    @Nullable
    public CacheResolver exceptionCacheResolver() {
        return null;
    }
}
