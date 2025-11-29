package org.springframework.boot.autoconfigure.cache;

import org.springframework.data.redis.cache.RedisCacheManager;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/RedisCacheManagerBuilderCustomizer.class */
public interface RedisCacheManagerBuilderCustomizer {
    void customize(RedisCacheManager.RedisCacheManagerBuilder builder);
}
