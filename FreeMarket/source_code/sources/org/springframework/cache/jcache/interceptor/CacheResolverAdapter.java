package org.springframework.cache.jcache.interceptor;

import java.util.Collection;
import java.util.Collections;
import javax.cache.annotation.CacheInvocationContext;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/CacheResolverAdapter.class */
class CacheResolverAdapter implements CacheResolver {
    private final javax.cache.annotation.CacheResolver target;

    public CacheResolverAdapter(javax.cache.annotation.CacheResolver target) {
        Assert.notNull(target, "JSR-107 CacheResolver is required");
        this.target = target;
    }

    protected javax.cache.annotation.CacheResolver getTarget() {
        return this.target;
    }

    @Override // org.springframework.cache.interceptor.CacheResolver
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        if (!(context instanceof CacheInvocationContext)) {
            throw new IllegalStateException("Unexpected context " + context);
        }
        CacheInvocationContext<?> cacheInvocationContext = (CacheInvocationContext) context;
        javax.cache.Cache<Object, Object> cache = this.target.resolveCache(cacheInvocationContext);
        if (cache == null) {
            throw new IllegalStateException("Could not resolve cache for " + context + " using " + this.target);
        }
        return Collections.singleton(new JCacheCache(cache));
    }
}
