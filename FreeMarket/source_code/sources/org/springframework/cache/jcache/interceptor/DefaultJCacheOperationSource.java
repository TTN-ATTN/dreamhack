package org.springframework.cache.jcache.interceptor;

import java.util.Collection;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.SupplierUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/DefaultJCacheOperationSource.class */
public class DefaultJCacheOperationSource extends AnnotationJCacheOperationSource implements BeanFactoryAware, SmartInitializingSingleton {

    @Nullable
    private SingletonSupplier<CacheManager> cacheManager;

    @Nullable
    private SingletonSupplier<CacheResolver> cacheResolver;

    @Nullable
    private SingletonSupplier<CacheResolver> exceptionCacheResolver;
    private SingletonSupplier<KeyGenerator> keyGenerator;
    private final SingletonSupplier<KeyGenerator> adaptedKeyGenerator;

    @Nullable
    private BeanFactory beanFactory;

    public DefaultJCacheOperationSource() {
        this.adaptedKeyGenerator = SingletonSupplier.of(() -> {
            return new KeyGeneratorAdapter(this, getKeyGenerator());
        });
        this.keyGenerator = SingletonSupplier.of(SimpleKeyGenerator::new);
    }

    public DefaultJCacheOperationSource(@Nullable Supplier<CacheManager> cacheManager, @Nullable Supplier<CacheResolver> cacheResolver, @Nullable Supplier<CacheResolver> exceptionCacheResolver, @Nullable Supplier<KeyGenerator> keyGenerator) {
        this.adaptedKeyGenerator = SingletonSupplier.of(() -> {
            return new KeyGeneratorAdapter(this, getKeyGenerator());
        });
        this.cacheManager = SingletonSupplier.ofNullable((Supplier) cacheManager);
        this.cacheResolver = SingletonSupplier.ofNullable((Supplier) cacheResolver);
        this.exceptionCacheResolver = SingletonSupplier.ofNullable((Supplier) exceptionCacheResolver);
        this.keyGenerator = new SingletonSupplier<>((Supplier) keyGenerator, SimpleKeyGenerator::new);
    }

    public void setCacheManager(@Nullable CacheManager cacheManager) {
        this.cacheManager = SingletonSupplier.ofNullable(cacheManager);
    }

    @Nullable
    public CacheManager getCacheManager() {
        return (CacheManager) SupplierUtils.resolve(this.cacheManager);
    }

    public void setCacheResolver(@Nullable CacheResolver cacheResolver) {
        this.cacheResolver = SingletonSupplier.ofNullable(cacheResolver);
    }

    @Nullable
    public CacheResolver getCacheResolver() {
        return (CacheResolver) SupplierUtils.resolve(this.cacheResolver);
    }

    public void setExceptionCacheResolver(@Nullable CacheResolver exceptionCacheResolver) {
        this.exceptionCacheResolver = SingletonSupplier.ofNullable(exceptionCacheResolver);
    }

    @Nullable
    public CacheResolver getExceptionCacheResolver() {
        return (CacheResolver) SupplierUtils.resolve(this.exceptionCacheResolver);
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = SingletonSupplier.of(keyGenerator);
    }

    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator.obtain();
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        Assert.notNull(getDefaultCacheResolver(), "Cache resolver should have been initialized");
    }

    @Override // org.springframework.cache.jcache.interceptor.AnnotationJCacheOperationSource
    protected <T> T getBean(Class<T> cls) {
        Assert.state(this.beanFactory != null, (Supplier<String>) () -> {
            return "BeanFactory required for resolution of [" + cls + "]";
        });
        try {
            return (T) this.beanFactory.getBean(cls);
        } catch (NoUniqueBeanDefinitionException e) {
            throw new IllegalStateException("No unique [" + cls.getName() + "] bean found in application context - mark one as primary, or declare a more specific implementation type for your cache", e);
        } catch (NoSuchBeanDefinitionException e2) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No bean of type [" + cls.getName() + "] found in application context", e2);
            }
            return (T) BeanUtils.instantiateClass(cls);
        }
    }

    protected CacheManager getDefaultCacheManager() {
        if (getCacheManager() == null) {
            Assert.state(this.beanFactory != null, "BeanFactory required for default CacheManager resolution");
            try {
                this.cacheManager = SingletonSupplier.of(this.beanFactory.getBean(CacheManager.class));
            } catch (NoUniqueBeanDefinitionException e) {
                throw new IllegalStateException("No unique bean of type CacheManager found. Mark one as primary or declare a specific CacheManager to use.");
            } catch (NoSuchBeanDefinitionException e2) {
                throw new IllegalStateException("No bean of type CacheManager found. Register a CacheManager bean or remove the @EnableCaching annotation from your configuration.");
            }
        }
        return getCacheManager();
    }

    @Override // org.springframework.cache.jcache.interceptor.AnnotationJCacheOperationSource
    protected CacheResolver getDefaultCacheResolver() {
        if (getCacheResolver() == null) {
            this.cacheResolver = SingletonSupplier.of(new SimpleCacheResolver(getDefaultCacheManager()));
        }
        return getCacheResolver();
    }

    @Override // org.springframework.cache.jcache.interceptor.AnnotationJCacheOperationSource
    protected CacheResolver getDefaultExceptionCacheResolver() {
        if (getExceptionCacheResolver() == null) {
            this.exceptionCacheResolver = SingletonSupplier.of(new LazyCacheResolver());
        }
        return getExceptionCacheResolver();
    }

    @Override // org.springframework.cache.jcache.interceptor.AnnotationJCacheOperationSource
    protected KeyGenerator getDefaultKeyGenerator() {
        return this.adaptedKeyGenerator.obtain();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/DefaultJCacheOperationSource$LazyCacheResolver.class */
    class LazyCacheResolver implements CacheResolver {
        private final SingletonSupplier<CacheResolver> cacheResolver = SingletonSupplier.of(() -> {
            return new SimpleExceptionCacheResolver(DefaultJCacheOperationSource.this.getDefaultCacheManager());
        });

        LazyCacheResolver() {
        }

        @Override // org.springframework.cache.interceptor.CacheResolver
        public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
            return this.cacheResolver.obtain().resolveCaches(context);
        }
    }
}
