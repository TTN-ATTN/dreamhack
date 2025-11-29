package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheRemoveAll;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.util.ExceptionTypeFilter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/CacheRemoveAllOperation.class */
class CacheRemoveAllOperation extends AbstractJCacheOperation<CacheRemoveAll> {
    private final ExceptionTypeFilter exceptionTypeFilter;

    public CacheRemoveAllOperation(CacheMethodDetails<CacheRemoveAll> methodDetails, CacheResolver cacheResolver) {
        super(methodDetails, cacheResolver);
        CacheRemoveAll ann = methodDetails.getCacheAnnotation();
        this.exceptionTypeFilter = createExceptionTypeFilter(ann.evictFor(), ann.noEvictFor());
    }

    @Override // org.springframework.cache.jcache.interceptor.AbstractJCacheOperation
    public ExceptionTypeFilter getExceptionTypeFilter() {
        return this.exceptionTypeFilter;
    }

    public boolean isEarlyRemove() {
        return !getCacheAnnotation().afterInvocation();
    }
}
