package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.interceptor.AbstractCacheInvoker;
import org.springframework.cache.interceptor.BasicOperation;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/JCacheAspectSupport.class */
public class JCacheAspectSupport extends AbstractCacheInvoker implements InitializingBean {

    @Nullable
    private JCacheOperationSource cacheOperationSource;

    @Nullable
    private CacheResultInterceptor cacheResultInterceptor;

    @Nullable
    private CachePutInterceptor cachePutInterceptor;

    @Nullable
    private CacheRemoveEntryInterceptor cacheRemoveEntryInterceptor;

    @Nullable
    private CacheRemoveAllInterceptor cacheRemoveAllInterceptor;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean initialized = false;

    public void setCacheOperationSource(JCacheOperationSource cacheOperationSource) {
        Assert.notNull(cacheOperationSource, "JCacheOperationSource must not be null");
        this.cacheOperationSource = cacheOperationSource;
    }

    public JCacheOperationSource getCacheOperationSource() {
        Assert.state(this.cacheOperationSource != null, "The 'cacheOperationSource' property is required: If there are no cacheable methods, then don't use a cache aspect.");
        return this.cacheOperationSource;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        getCacheOperationSource();
        this.cacheResultInterceptor = new CacheResultInterceptor(getErrorHandler());
        this.cachePutInterceptor = new CachePutInterceptor(getErrorHandler());
        this.cacheRemoveEntryInterceptor = new CacheRemoveEntryInterceptor(getErrorHandler());
        this.cacheRemoveAllInterceptor = new CacheRemoveAllInterceptor(getErrorHandler());
        this.initialized = true;
    }

    @Nullable
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        if (this.initialized) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
            JCacheOperation<?> operation = getCacheOperationSource().getCacheOperation(method, targetClass);
            if (operation != null) {
                CacheOperationInvocationContext<?> context = createCacheOperationInvocationContext(target, args, operation);
                return execute(context, invoker);
            }
        }
        return invoker.invoke();
    }

    private CacheOperationInvocationContext<?> createCacheOperationInvocationContext(Object target, Object[] args, JCacheOperation<?> operation) {
        return new DefaultCacheInvocationContext(operation, target, args);
    }

    @Nullable
    private Object execute(CacheOperationInvocationContext<?> context, CacheOperationInvoker invoker) {
        CacheOperationInvoker adapter = new CacheOperationInvokerAdapter(invoker);
        BasicOperation operation = context.getOperation();
        if (operation instanceof CacheResultOperation) {
            Assert.state(this.cacheResultInterceptor != null, "No CacheResultInterceptor");
            return this.cacheResultInterceptor.invoke(context, adapter);
        }
        if (operation instanceof CachePutOperation) {
            Assert.state(this.cachePutInterceptor != null, "No CachePutInterceptor");
            return this.cachePutInterceptor.invoke(context, adapter);
        }
        if (operation instanceof CacheRemoveOperation) {
            Assert.state(this.cacheRemoveEntryInterceptor != null, "No CacheRemoveEntryInterceptor");
            return this.cacheRemoveEntryInterceptor.invoke(context, adapter);
        }
        if (operation instanceof CacheRemoveAllOperation) {
            Assert.state(this.cacheRemoveAllInterceptor != null, "No CacheRemoveAllInterceptor");
            return this.cacheRemoveAllInterceptor.invoke(context, adapter);
        }
        throw new IllegalArgumentException("Cannot handle " + operation);
    }

    @Nullable
    protected Object invokeOperation(CacheOperationInvoker invoker) {
        return invoker.invoke();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/JCacheAspectSupport$CacheOperationInvokerAdapter.class */
    private class CacheOperationInvokerAdapter implements CacheOperationInvoker {
        private final CacheOperationInvoker delegate;

        public CacheOperationInvokerAdapter(CacheOperationInvoker delegate) {
            this.delegate = delegate;
        }

        @Override // org.springframework.cache.interceptor.CacheOperationInvoker
        public Object invoke() throws CacheOperationInvoker.ThrowableWrapper {
            return JCacheAspectSupport.this.invokeOperation(this.delegate);
        }
    }
}
