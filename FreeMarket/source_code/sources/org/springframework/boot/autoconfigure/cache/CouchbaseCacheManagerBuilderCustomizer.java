package org.springframework.boot.autoconfigure.cache;

import org.springframework.data.couchbase.cache.CouchbaseCacheManager;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/CouchbaseCacheManagerBuilderCustomizer.class */
public interface CouchbaseCacheManagerBuilderCustomizer {
    void customize(CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder);
}
