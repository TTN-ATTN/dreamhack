package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheRemove;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/CacheRemoveEntryInterceptor.class */
class CacheRemoveEntryInterceptor extends AbstractKeyCacheInterceptor<CacheRemoveOperation, CacheRemove> {
    protected CacheRemoveEntryInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override // org.springframework.cache.jcache.interceptor.AbstractCacheInterceptor
    protected Object invoke(CacheOperationInvocationContext<CacheRemoveOperation> context, CacheOperationInvoker invoker) {
        CacheRemoveOperation operation = (CacheRemoveOperation) context.getOperation();
        boolean earlyRemove = operation.isEarlyRemove();
        if (earlyRemove) {
            removeValue(context);
        }
        try {
            Object result = invoker.invoke();
            if (!earlyRemove) {
                removeValue(context);
            }
            return result;
        } catch (CacheOperationInvoker.ThrowableWrapper wrapperException) {
            Throwable ex = wrapperException.getOriginal();
            if (!earlyRemove && operation.getExceptionTypeFilter().match(ex.getClass())) {
                removeValue(context);
            }
            throw wrapperException;
        }
    }

    private void removeValue(CacheOperationInvocationContext<CacheRemoveOperation> context) {
        Object key = generateKey(context);
        Cache cache = resolveCache(context);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Invalidating key [" + key + "] on cache '" + cache.getName() + "' for operation " + context.getOperation());
        }
        doEvict(cache, key, ((CacheRemoveOperation) context.getOperation()).isEarlyRemove());
    }
}
