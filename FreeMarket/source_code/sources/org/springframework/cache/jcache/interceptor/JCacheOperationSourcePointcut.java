package org.springframework.cache.jcache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/JCacheOperationSourcePointcut.class */
public abstract class JCacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {
    @Nullable
    protected abstract JCacheOperationSource getCacheOperationSource();

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        JCacheOperationSource cas = getCacheOperationSource();
        return (cas == null || cas.getCacheOperation(method, targetClass) == null) ? false : true;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof JCacheOperationSourcePointcut)) {
            return false;
        }
        JCacheOperationSourcePointcut otherPc = (JCacheOperationSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(getCacheOperationSource(), otherPc.getCacheOperationSource());
    }

    public int hashCode() {
        return JCacheOperationSourcePointcut.class.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + getCacheOperationSource();
    }
}
