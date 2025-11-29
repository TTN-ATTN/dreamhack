package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.CachePut;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/CachePutInterceptor.class */
class CachePutInterceptor extends AbstractKeyCacheInterceptor<CachePutOperation, CachePut> {
    public CachePutInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override // org.springframework.cache.jcache.interceptor.AbstractCacheInterceptor
    protected Object invoke(CacheOperationInvocationContext<CachePutOperation> context, CacheOperationInvoker invoker) {
        CachePutOperation operation = (CachePutOperation) context.getOperation();
        CacheKeyInvocationContext<CachePut> invocationContext = createCacheKeyInvocationContext(context);
        boolean earlyPut = operation.isEarlyPut();
        Object value = invocationContext.getValueParameter().getValue();
        if (earlyPut) {
            cacheValue(context, value);
        }
        try {
            Object result = invoker.invoke();
            if (!earlyPut) {
                cacheValue(context, value);
            }
            return result;
        } catch (CacheOperationInvoker.ThrowableWrapper ex) {
            Throwable original = ex.getOriginal();
            if (!earlyPut && operation.getExceptionTypeFilter().match(original.getClass())) {
                cacheValue(context, value);
            }
            throw ex;
        }
    }

    protected void cacheValue(CacheOperationInvocationContext<CachePutOperation> context, Object value) {
        Object key = generateKey(context);
        Cache cache = resolveCache(context);
        doPut(cache, key, value);
    }
}
