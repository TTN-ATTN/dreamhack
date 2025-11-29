package org.springframework.cache.jcache;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.cache.CacheManager;
import javax.cache.Caching;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/JCacheCacheManager.class */
public class JCacheCacheManager extends AbstractTransactionSupportingCacheManager {

    @Nullable
    private CacheManager cacheManager;
    private boolean allowNullValues = true;

    public JCacheCacheManager() {
    }

    public JCacheCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheManager(@Nullable CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Nullable
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    @Override // org.springframework.cache.support.AbstractCacheManager, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (getCacheManager() == null) {
            setCacheManager(Caching.getCachingProvider().getCacheManager());
        }
        super.afterPropertiesSet();
    }

    @Override // org.springframework.cache.support.AbstractCacheManager
    protected Collection<Cache> loadCaches() {
        CacheManager cacheManager = getCacheManager();
        Assert.state(cacheManager != null, "No CacheManager set");
        Collection<Cache> caches = new LinkedHashSet<>();
        for (String cacheName : cacheManager.getCacheNames()) {
            javax.cache.Cache<Object, Object> jcache = cacheManager.getCache(cacheName);
            caches.add(new JCacheCache(jcache, isAllowNullValues()));
        }
        return caches;
    }

    @Override // org.springframework.cache.support.AbstractCacheManager
    protected Cache getMissingCache(String name) {
        CacheManager cacheManager = getCacheManager();
        Assert.state(cacheManager != null, "No CacheManager set");
        javax.cache.Cache<Object, Object> jcache = cacheManager.getCache(name);
        if (jcache != null) {
            return new JCacheCache(jcache, isAllowNullValues());
        }
        return null;
    }
}
