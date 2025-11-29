package org.springframework.cache.ehcache;

import java.util.Collection;
import java.util.LinkedHashSet;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/ehcache/EhCacheCacheManager.class */
public class EhCacheCacheManager extends AbstractTransactionSupportingCacheManager {

    @Nullable
    private CacheManager cacheManager;

    public EhCacheCacheManager() {
    }

    public EhCacheCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheManager(@Nullable CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Nullable
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    @Override // org.springframework.cache.support.AbstractCacheManager, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (getCacheManager() == null) {
            setCacheManager(EhCacheManagerUtils.buildCacheManager());
        }
        super.afterPropertiesSet();
    }

    @Override // org.springframework.cache.support.AbstractCacheManager
    protected Collection<Cache> loadCaches() {
        CacheManager cacheManager = getCacheManager();
        Assert.state(cacheManager != null, "No CacheManager set");
        Status status = cacheManager.getStatus();
        if (!Status.STATUS_ALIVE.equals(status)) {
            throw new IllegalStateException("An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
        }
        String[] names = getCacheManager().getCacheNames();
        Collection<Cache> caches = new LinkedHashSet<>(names.length);
        for (String name : names) {
            caches.add(new EhCacheCache(getCacheManager().getEhcache(name)));
        }
        return caches;
    }

    @Override // org.springframework.cache.support.AbstractCacheManager
    protected Cache getMissingCache(String name) {
        CacheManager cacheManager = getCacheManager();
        Assert.state(cacheManager != null, "No CacheManager set");
        Ehcache ehcache = cacheManager.getEhcache(name);
        if (ehcache != null) {
            return new EhCacheCache(ehcache);
        }
        return null;
    }
}
