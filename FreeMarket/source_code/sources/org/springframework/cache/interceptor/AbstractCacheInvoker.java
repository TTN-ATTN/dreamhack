package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/AbstractCacheInvoker.class */
public abstract class AbstractCacheInvoker {
    protected SingletonSupplier<CacheErrorHandler> errorHandler;

    protected AbstractCacheInvoker() {
        this.errorHandler = SingletonSupplier.of(SimpleCacheErrorHandler::new);
    }

    protected AbstractCacheInvoker(CacheErrorHandler errorHandler) {
        this.errorHandler = SingletonSupplier.of(errorHandler);
    }

    public void setErrorHandler(CacheErrorHandler errorHandler) {
        this.errorHandler = SingletonSupplier.of(errorHandler);
    }

    public CacheErrorHandler getErrorHandler() {
        return this.errorHandler.obtain();
    }

    @Nullable
    protected Cache.ValueWrapper doGet(Cache cache, Object key) {
        try {
            return cache.get(key);
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheGetError(ex, cache, key);
            return null;
        }
    }

    protected void doPut(Cache cache, Object key, @Nullable Object result) {
        try {
            cache.put(key, result);
        } catch (RuntimeException ex) {
            getErrorHandler().handleCachePutError(ex, cache, key, result);
        }
    }

    protected void doEvict(Cache cache, Object key, boolean immediate) {
        try {
            if (immediate) {
                cache.evictIfPresent(key);
            } else {
                cache.evict(key);
            }
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheEvictError(ex, cache, key);
        }
    }

    protected void doClear(Cache cache, boolean immediate) {
        try {
            if (immediate) {
                cache.invalidate();
            } else {
                cache.clear();
            }
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheClearError(ex, cache);
        }
    }
}
