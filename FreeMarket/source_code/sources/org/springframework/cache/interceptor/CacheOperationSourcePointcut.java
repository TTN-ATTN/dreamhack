package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheOperationSourcePointcut.class */
abstract class CacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {
    @Nullable
    protected abstract CacheOperationSource getCacheOperationSource();

    protected CacheOperationSourcePointcut() {
        setClassFilter(new CacheOperationSourceClassFilter());
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        CacheOperationSource cas = getCacheOperationSource();
        return (cas == null || CollectionUtils.isEmpty(cas.getCacheOperations(method, targetClass))) ? false : true;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CacheOperationSourcePointcut)) {
            return false;
        }
        CacheOperationSourcePointcut otherPc = (CacheOperationSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(getCacheOperationSource(), otherPc.getCacheOperationSource());
    }

    public int hashCode() {
        return CacheOperationSourcePointcut.class.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + getCacheOperationSource();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheOperationSourcePointcut$CacheOperationSourceClassFilter.class */
    private class CacheOperationSourceClassFilter implements ClassFilter {
        private CacheOperationSourceClassFilter() {
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            if (CacheManager.class.isAssignableFrom(clazz)) {
                return false;
            }
            CacheOperationSource cas = CacheOperationSourcePointcut.this.getCacheOperationSource();
            return cas == null || cas.isCandidateClass(clazz);
        }
    }
}
