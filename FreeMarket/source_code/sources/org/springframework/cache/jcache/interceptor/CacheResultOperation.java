package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResult;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;
import org.springframework.util.ExceptionTypeFilter;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/CacheResultOperation.class */
class CacheResultOperation extends AbstractJCacheKeyOperation<CacheResult> {
    private final ExceptionTypeFilter exceptionTypeFilter;

    @Nullable
    private final CacheResolver exceptionCacheResolver;

    @Nullable
    private final String exceptionCacheName;

    public CacheResultOperation(CacheMethodDetails<CacheResult> methodDetails, CacheResolver cacheResolver, KeyGenerator keyGenerator, @Nullable CacheResolver exceptionCacheResolver) {
        super(methodDetails, cacheResolver, keyGenerator);
        CacheResult ann = methodDetails.getCacheAnnotation();
        this.exceptionTypeFilter = createExceptionTypeFilter(ann.cachedExceptions(), ann.nonCachedExceptions());
        this.exceptionCacheResolver = exceptionCacheResolver;
        this.exceptionCacheName = StringUtils.hasText(ann.exceptionCacheName()) ? ann.exceptionCacheName() : null;
    }

    @Override // org.springframework.cache.jcache.interceptor.AbstractJCacheOperation
    public ExceptionTypeFilter getExceptionTypeFilter() {
        return this.exceptionTypeFilter;
    }

    public boolean isAlwaysInvoked() {
        return ((CacheResult) getCacheAnnotation()).skipGet();
    }

    @Nullable
    public CacheResolver getExceptionCacheResolver() {
        return this.exceptionCacheResolver;
    }

    @Nullable
    public String getExceptionCacheName() {
        return this.exceptionCacheName;
    }
}
