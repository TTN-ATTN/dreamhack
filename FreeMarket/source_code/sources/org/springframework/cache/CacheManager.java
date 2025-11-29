package org.springframework.cache;

import java.util.Collection;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/CacheManager.class */
public interface CacheManager {
    @Nullable
    Cache getCache(String name);

    Collection<String> getCacheNames();
}
