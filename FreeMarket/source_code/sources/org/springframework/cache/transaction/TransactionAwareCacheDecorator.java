package org.springframework.cache.transaction;

import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/transaction/TransactionAwareCacheDecorator.class */
public class TransactionAwareCacheDecorator implements Cache {
    private final Cache targetCache;

    public TransactionAwareCacheDecorator(Cache targetCache) {
        Assert.notNull(targetCache, "Target Cache must not be null");
        this.targetCache = targetCache;
    }

    public Cache getTargetCache() {
        return this.targetCache;
    }

    @Override // org.springframework.cache.Cache
    public String getName() {
        return this.targetCache.getName();
    }

    @Override // org.springframework.cache.Cache
    public Object getNativeCache() {
        return this.targetCache.getNativeCache();
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        return this.targetCache.get(key);
    }

    @Override // org.springframework.cache.Cache
    public <T> T get(Object obj, @Nullable Class<T> cls) {
        return (T) this.targetCache.get(obj, cls);
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public <T> T get(Object obj, Callable<T> callable) {
        return (T) this.targetCache.get(obj, callable);
    }

    @Override // org.springframework.cache.Cache
    public void put(final Object key, @Nullable final Object value) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() { // from class: org.springframework.cache.transaction.TransactionAwareCacheDecorator.1
                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.put(key, value);
                }
            });
        } else {
            this.targetCache.put(key, value);
        }
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        return this.targetCache.putIfAbsent(key, value);
    }

    @Override // org.springframework.cache.Cache
    public void evict(final Object key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() { // from class: org.springframework.cache.transaction.TransactionAwareCacheDecorator.2
                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.evict(key);
                }
            });
        } else {
            this.targetCache.evict(key);
        }
    }

    @Override // org.springframework.cache.Cache
    public boolean evictIfPresent(Object key) {
        return this.targetCache.evictIfPresent(key);
    }

    @Override // org.springframework.cache.Cache
    public void clear() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() { // from class: org.springframework.cache.transaction.TransactionAwareCacheDecorator.3
                public void afterCommit() {
                    TransactionAwareCacheDecorator.this.targetCache.clear();
                }
            });
        } else {
            this.targetCache.clear();
        }
    }

    @Override // org.springframework.cache.Cache
    public boolean invalidate() {
        return this.targetCache.invalidate();
    }
}
