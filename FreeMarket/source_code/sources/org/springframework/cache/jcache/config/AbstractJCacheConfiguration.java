package org.springframework.cache.jcache.config;

import java.util.function.Supplier;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.lang.Nullable;

@Configuration(proxyBeanMethods = false)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/config/AbstractJCacheConfiguration.class */
public abstract class AbstractJCacheConfiguration extends AbstractCachingConfiguration {

    @Nullable
    protected Supplier<CacheResolver> exceptionCacheResolver;

    @Override // org.springframework.cache.annotation.AbstractCachingConfiguration
    protected void useCachingConfigurer(AbstractCachingConfiguration.CachingConfigurerSupplier cachingConfigurerSupplier) {
        super.useCachingConfigurer(cachingConfigurerSupplier);
        this.exceptionCacheResolver = cachingConfigurerSupplier.adapt(config -> {
            if (config instanceof JCacheConfigurer) {
                return ((JCacheConfigurer) config).exceptionCacheResolver();
            }
            return null;
        });
    }

    @Bean(name = {"jCacheOperationSource"})
    @Role(2)
    public JCacheOperationSource cacheOperationSource() {
        return new DefaultJCacheOperationSource(this.cacheManager, this.cacheResolver, this.exceptionCacheResolver, this.keyGenerator);
    }
}
