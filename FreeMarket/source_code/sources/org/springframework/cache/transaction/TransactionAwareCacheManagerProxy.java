package org.springframework.cache.transaction;

import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/transaction/TransactionAwareCacheManagerProxy.class */
public class TransactionAwareCacheManagerProxy implements CacheManager, InitializingBean {

    @Nullable
    private CacheManager targetCacheManager;

    public TransactionAwareCacheManagerProxy() {
    }

    public TransactionAwareCacheManagerProxy(CacheManager targetCacheManager) {
        Assert.notNull(targetCacheManager, "Target CacheManager must not be null");
        this.targetCacheManager = targetCacheManager;
    }

    public void setTargetCacheManager(CacheManager targetCacheManager) {
        this.targetCacheManager = targetCacheManager;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.targetCacheManager == null) {
            throw new IllegalArgumentException("Property 'targetCacheManager' is required");
        }
    }

    @Override // org.springframework.cache.CacheManager
    @Nullable
    public Cache getCache(String name) {
        Assert.state(this.targetCacheManager != null, "No target CacheManager set");
        Cache targetCache = this.targetCacheManager.getCache(name);
        if (targetCache != null) {
            return new TransactionAwareCacheDecorator(targetCache);
        }
        return null;
    }

    @Override // org.springframework.cache.CacheManager
    public Collection<String> getCacheNames() {
        Assert.state(this.targetCacheManager != null, "No target CacheManager set");
        return this.targetCacheManager.getCacheNames();
    }
}
