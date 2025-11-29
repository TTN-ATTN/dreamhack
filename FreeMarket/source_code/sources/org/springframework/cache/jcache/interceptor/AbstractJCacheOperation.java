package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheValue;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.util.Assert;
import org.springframework.util.ExceptionTypeFilter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/AbstractJCacheOperation.class */
abstract class AbstractJCacheOperation<A extends Annotation> implements JCacheOperation<A> {
    private final CacheMethodDetails<A> methodDetails;
    private final CacheResolver cacheResolver;
    protected final List<CacheParameterDetail> allParameterDetails;

    public abstract ExceptionTypeFilter getExceptionTypeFilter();

    protected AbstractJCacheOperation(CacheMethodDetails<A> methodDetails, CacheResolver cacheResolver) {
        Assert.notNull(methodDetails, "CacheMethodDetails must not be null");
        Assert.notNull(cacheResolver, "CacheResolver must not be null");
        this.methodDetails = methodDetails;
        this.cacheResolver = cacheResolver;
        this.allParameterDetails = initializeAllParameterDetails(methodDetails.getMethod());
    }

    private static List<CacheParameterDetail> initializeAllParameterDetails(Method method) {
        int parameterCount = method.getParameterCount();
        List<CacheParameterDetail> result = new ArrayList<>(parameterCount);
        for (int i = 0; i < parameterCount; i++) {
            CacheParameterDetail detail = new CacheParameterDetail(method, i);
            result.add(detail);
        }
        return result;
    }

    public Method getMethod() {
        return this.methodDetails.getMethod();
    }

    public Set<Annotation> getAnnotations() {
        return this.methodDetails.getAnnotations();
    }

    public A getCacheAnnotation() {
        return (A) this.methodDetails.getCacheAnnotation();
    }

    public String getCacheName() {
        return this.methodDetails.getCacheName();
    }

    @Override // org.springframework.cache.interceptor.BasicOperation
    public Set<String> getCacheNames() {
        return Collections.singleton(getCacheName());
    }

    @Override // org.springframework.cache.jcache.interceptor.JCacheOperation
    public CacheResolver getCacheResolver() {
        return this.cacheResolver;
    }

    @Override // org.springframework.cache.jcache.interceptor.JCacheOperation
    public CacheInvocationParameter[] getAllParameters(Object... values) {
        if (this.allParameterDetails.size() != values.length) {
            throw new IllegalStateException("Values mismatch, operation has " + this.allParameterDetails.size() + " parameter(s) but got " + values.length + " value(s)");
        }
        List<CacheInvocationParameter> result = new ArrayList<>();
        for (int i = 0; i < this.allParameterDetails.size(); i++) {
            result.add(this.allParameterDetails.get(i).toCacheInvocationParameter(values[i]));
        }
        return (CacheInvocationParameter[]) result.toArray(new CacheInvocationParameter[0]);
    }

    protected ExceptionTypeFilter createExceptionTypeFilter(Class<? extends Throwable>[] includes, Class<? extends Throwable>[] excludes) {
        return new ExceptionTypeFilter(Arrays.asList(includes), Arrays.asList(excludes), true);
    }

    public String toString() {
        return getOperationDescription().append(']').toString();
    }

    protected StringBuilder getOperationDescription() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().getSimpleName());
        result.append('[');
        result.append(this.methodDetails);
        return result;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/AbstractJCacheOperation$CacheParameterDetail.class */
    protected static class CacheParameterDetail {
        private final Class<?> rawType;
        private final Set<Annotation> annotations = new LinkedHashSet();
        private final int parameterPosition;
        private final boolean isKey;
        private final boolean isValue;

        public CacheParameterDetail(Method method, int parameterPosition) {
            this.rawType = method.getParameterTypes()[parameterPosition];
            boolean foundKeyAnnotation = false;
            boolean foundValueAnnotation = false;
            for (Annotation annotation : method.getParameterAnnotations()[parameterPosition]) {
                this.annotations.add(annotation);
                foundKeyAnnotation = CacheKey.class.isAssignableFrom(annotation.annotationType()) ? true : foundKeyAnnotation;
                if (CacheValue.class.isAssignableFrom(annotation.annotationType())) {
                    foundValueAnnotation = true;
                }
            }
            this.parameterPosition = parameterPosition;
            this.isKey = foundKeyAnnotation;
            this.isValue = foundValueAnnotation;
        }

        public int getParameterPosition() {
            return this.parameterPosition;
        }

        protected boolean isKey() {
            return this.isKey;
        }

        protected boolean isValue() {
            return this.isValue;
        }

        public CacheInvocationParameter toCacheInvocationParameter(Object value) {
            return new CacheInvocationParameterImpl(this, value);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/AbstractJCacheOperation$CacheInvocationParameterImpl.class */
    protected static class CacheInvocationParameterImpl implements CacheInvocationParameter {
        private final CacheParameterDetail detail;
        private final Object value;

        public CacheInvocationParameterImpl(CacheParameterDetail detail, Object value) {
            this.detail = detail;
            this.value = value;
        }

        public Class<?> getRawType() {
            return this.detail.rawType;
        }

        public Object getValue() {
            return this.value;
        }

        public Set<Annotation> getAnnotations() {
            return this.detail.annotations;
        }

        public int getParameterPosition() {
            return this.detail.parameterPosition;
        }
    }
}
