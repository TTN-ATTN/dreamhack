package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import org.springframework.cache.interceptor.BasicOperation;
import org.springframework.cache.interceptor.CacheResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/JCacheOperation.class */
public interface JCacheOperation<A extends Annotation> extends BasicOperation, CacheMethodDetails<A> {
    CacheResolver getCacheResolver();

    CacheInvocationParameter[] getAllParameters(Object... values);
}
