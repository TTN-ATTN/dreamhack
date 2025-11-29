package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/AnnotationJCacheOperationSource.class */
public abstract class AnnotationJCacheOperationSource extends AbstractFallbackJCacheOperationSource {
    protected abstract <T> T getBean(Class<T> type);

    protected abstract CacheResolver getDefaultCacheResolver();

    protected abstract CacheResolver getDefaultExceptionCacheResolver();

    protected abstract KeyGenerator getDefaultKeyGenerator();

    @Override // org.springframework.cache.jcache.interceptor.AbstractFallbackJCacheOperationSource
    protected JCacheOperation<?> findCacheOperation(Method method, @Nullable Class<?> targetType) {
        CacheResult cacheResult = method.getAnnotation(CacheResult.class);
        CachePut cachePut = method.getAnnotation(CachePut.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        int found = countNonNull(cacheResult, cachePut, cacheRemove, cacheRemoveAll);
        if (found == 0) {
            return null;
        }
        if (found > 1) {
            throw new IllegalStateException("More than one cache annotation found on '" + method + "'");
        }
        CacheDefaults defaults = getCacheDefaults(method, targetType);
        if (cacheResult != null) {
            return createCacheResultOperation(method, defaults, cacheResult);
        }
        if (cachePut != null) {
            return createCachePutOperation(method, defaults, cachePut);
        }
        if (cacheRemove != null) {
            return createCacheRemoveOperation(method, defaults, cacheRemove);
        }
        return createCacheRemoveAllOperation(method, defaults, cacheRemoveAll);
    }

    @Nullable
    protected CacheDefaults getCacheDefaults(Method method, @Nullable Class<?> targetType) {
        CacheDefaults annotation = method.getDeclaringClass().getAnnotation(CacheDefaults.class);
        if (annotation != null) {
            return annotation;
        }
        if (targetType != null) {
            return targetType.getAnnotation(CacheDefaults.class);
        }
        return null;
    }

    protected CacheResultOperation createCacheResultOperation(Method method, @Nullable CacheDefaults defaults, CacheResult ann) {
        String cacheName = determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        KeyGenerator keyGenerator = determineKeyGenerator(defaults, ann.cacheKeyGenerator());
        CacheMethodDetails<?> cacheMethodDetailsCreateMethodDetails = createMethodDetails(method, ann, cacheName);
        CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, cacheMethodDetailsCreateMethodDetails);
        CacheResolver exceptionCacheResolver = null;
        String exceptionCacheName = ann.exceptionCacheName();
        if (StringUtils.hasText(exceptionCacheName)) {
            exceptionCacheResolver = getExceptionCacheResolver(cacheResolverFactory, cacheMethodDetailsCreateMethodDetails);
        }
        return new CacheResultOperation(cacheMethodDetailsCreateMethodDetails, cacheResolver, keyGenerator, exceptionCacheResolver);
    }

    protected CachePutOperation createCachePutOperation(Method method, @Nullable CacheDefaults defaults, CachePut ann) {
        String cacheName = determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        KeyGenerator keyGenerator = determineKeyGenerator(defaults, ann.cacheKeyGenerator());
        CacheMethodDetails<?> cacheMethodDetailsCreateMethodDetails = createMethodDetails(method, ann, cacheName);
        CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, cacheMethodDetailsCreateMethodDetails);
        return new CachePutOperation(cacheMethodDetailsCreateMethodDetails, cacheResolver, keyGenerator);
    }

    protected CacheRemoveOperation createCacheRemoveOperation(Method method, @Nullable CacheDefaults defaults, CacheRemove ann) {
        String cacheName = determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        KeyGenerator keyGenerator = determineKeyGenerator(defaults, ann.cacheKeyGenerator());
        CacheMethodDetails<?> cacheMethodDetailsCreateMethodDetails = createMethodDetails(method, ann, cacheName);
        CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, cacheMethodDetailsCreateMethodDetails);
        return new CacheRemoveOperation(cacheMethodDetailsCreateMethodDetails, cacheResolver, keyGenerator);
    }

    protected CacheRemoveAllOperation createCacheRemoveAllOperation(Method method, @Nullable CacheDefaults defaults, CacheRemoveAll ann) {
        String cacheName = determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        CacheMethodDetails<?> cacheMethodDetailsCreateMethodDetails = createMethodDetails(method, ann, cacheName);
        CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, cacheMethodDetailsCreateMethodDetails);
        return new CacheRemoveAllOperation(cacheMethodDetailsCreateMethodDetails, cacheResolver);
    }

    private <A extends Annotation> CacheMethodDetails<A> createMethodDetails(Method method, A annotation, String cacheName) {
        return new DefaultCacheMethodDetails(method, annotation, cacheName);
    }

    protected CacheResolver getCacheResolver(@Nullable CacheResolverFactory factory, CacheMethodDetails<?> details) {
        if (factory != null) {
            javax.cache.annotation.CacheResolver cacheResolver = factory.getCacheResolver(details);
            return new CacheResolverAdapter(cacheResolver);
        }
        return getDefaultCacheResolver();
    }

    protected CacheResolver getExceptionCacheResolver(@Nullable CacheResolverFactory factory, CacheMethodDetails<CacheResult> details) {
        if (factory != null) {
            javax.cache.annotation.CacheResolver cacheResolver = factory.getExceptionCacheResolver(details);
            return new CacheResolverAdapter(cacheResolver);
        }
        return getDefaultExceptionCacheResolver();
    }

    @Nullable
    protected CacheResolverFactory determineCacheResolverFactory(@Nullable CacheDefaults defaults, Class<? extends CacheResolverFactory> candidate) {
        if (candidate != CacheResolverFactory.class) {
            return (CacheResolverFactory) getBean(candidate);
        }
        if (defaults != null && defaults.cacheResolverFactory() != CacheResolverFactory.class) {
            return (CacheResolverFactory) getBean(defaults.cacheResolverFactory());
        }
        return null;
    }

    protected KeyGenerator determineKeyGenerator(@Nullable CacheDefaults defaults, Class<? extends CacheKeyGenerator> candidate) {
        if (candidate != CacheKeyGenerator.class) {
            return new KeyGeneratorAdapter(this, (CacheKeyGenerator) getBean(candidate));
        }
        if (defaults != null && CacheKeyGenerator.class != defaults.cacheKeyGenerator()) {
            return new KeyGeneratorAdapter(this, (CacheKeyGenerator) getBean(defaults.cacheKeyGenerator()));
        }
        return getDefaultKeyGenerator();
    }

    protected String determineCacheName(Method method, @Nullable CacheDefaults defaults, String candidate) {
        if (StringUtils.hasText(candidate)) {
            return candidate;
        }
        if (defaults != null && StringUtils.hasText(defaults.cacheName())) {
            return defaults.cacheName();
        }
        return generateDefaultCacheName(method);
    }

    protected String generateDefaultCacheName(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<String> parameters = new ArrayList<>(parameterTypes.length);
        for (Class<?> parameterType : parameterTypes) {
            parameters.add(parameterType.getName());
        }
        return method.getDeclaringClass().getName() + '.' + method.getName() + '(' + StringUtils.collectionToCommaDelimitedString(parameters) + ')';
    }

    private int countNonNull(Object... instances) {
        int result = 0;
        for (Object instance : instances) {
            if (instance != null) {
                result++;
            }
        }
        return result;
    }
}
