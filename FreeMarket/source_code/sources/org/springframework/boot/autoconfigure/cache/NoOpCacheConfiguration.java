package org.springframework.boot.autoconfigure.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean({CacheManager.class})
@Configuration(proxyBeanMethods = false)
@Conditional({CacheCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/NoOpCacheConfiguration.class */
class NoOpCacheConfiguration {
    NoOpCacheConfiguration() {
    }

    @Bean
    NoOpCacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
