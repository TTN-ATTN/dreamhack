package org.springframework.boot.autoconfigure.cache;

import org.springframework.cache.CacheManager;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/CacheManagerCustomizer.class */
public interface CacheManagerCustomizer<T extends CacheManager> {
    void customize(T cacheManager);
}
