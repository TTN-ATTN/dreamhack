package org.springframework.boot.autoconfigure.cache;

import net.sf.ehcache.Cache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ResourceCondition;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Cache.class, EhCacheCacheManager.class})
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class, ConfigAvailableCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/EhCacheCacheConfiguration.class */
class EhCacheCacheConfiguration {
    EhCacheCacheConfiguration() {
    }

    @Bean
    EhCacheCacheManager cacheManager(CacheManagerCustomizers customizers, net.sf.ehcache.CacheManager ehCacheCacheManager) {
        return (EhCacheCacheManager) customizers.customize(new EhCacheCacheManager(ehCacheCacheManager));
    }

    @ConditionalOnMissingBean
    @Bean
    net.sf.ehcache.CacheManager ehCacheCacheManager(CacheProperties cacheProperties) {
        Resource location = cacheProperties.resolveConfigLocation(cacheProperties.getEhcache().getConfig());
        if (location != null) {
            return EhCacheManagerUtils.buildCacheManager(location);
        }
        return EhCacheManagerUtils.buildCacheManager();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/EhCacheCacheConfiguration$ConfigAvailableCondition.class */
    static class ConfigAvailableCondition extends ResourceCondition {
        ConfigAvailableCondition() {
            super("EhCache", "spring.cache.ehcache.config", "classpath:/ehcache.xml");
        }
    }
}
