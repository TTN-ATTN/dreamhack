package org.springframework.boot.autoconfigure.cache;

import java.util.Properties;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/JCachePropertiesCustomizer.class */
interface JCachePropertiesCustomizer {
    void customize(CacheProperties cacheProperties, Properties properties);
}
