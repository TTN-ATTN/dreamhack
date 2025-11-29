package org.springframework.cache.ehcache;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;
import net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/ehcache/EhCacheFactoryBean.class */
public class EhCacheFactoryBean extends CacheConfiguration implements FactoryBean<Ehcache>, BeanNameAware, InitializingBean {

    @Nullable
    private CacheManager cacheManager;

    @Nullable
    private CacheEntryFactory cacheEntryFactory;

    @Nullable
    private BootstrapCacheLoader bootstrapCacheLoader;

    @Nullable
    private Set<CacheEventListener> cacheEventListeners;

    @Nullable
    private String beanName;

    @Nullable
    private Ehcache cache;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean blocking = false;
    private boolean disabled = false;

    public EhCacheFactoryBean() {
        setMaxEntriesLocalHeap(AbstractComponentTracker.LINGERING_TIMEOUT);
        setMaxEntriesLocalDisk(10000000L);
        setTimeToLiveSeconds(120L);
        setTimeToIdleSeconds(120L);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheName(String cacheName) {
        setName(cacheName);
    }

    public void setTimeToLive(int timeToLive) {
        setTimeToLiveSeconds(timeToLive);
    }

    public void setTimeToIdle(int timeToIdle) {
        setTimeToIdleSeconds(timeToIdle);
    }

    public void setDiskSpoolBufferSize(int diskSpoolBufferSize) {
        setDiskSpoolBufferSizeMB(diskSpoolBufferSize);
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public void setCacheEntryFactory(CacheEntryFactory cacheEntryFactory) {
        this.cacheEntryFactory = cacheEntryFactory;
    }

    public void setBootstrapCacheLoader(BootstrapCacheLoader bootstrapCacheLoader) {
        this.bootstrapCacheLoader = bootstrapCacheLoader;
    }

    public void setCacheEventListeners(Set<CacheEventListener> cacheEventListeners) {
        this.cacheEventListeners = cacheEventListeners;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws CacheException {
        Ehcache rawCache;
        String cacheName = getName();
        if (cacheName == null) {
            cacheName = this.beanName;
            if (cacheName != null) {
                setName(cacheName);
            }
        }
        if (this.cacheManager == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using default EhCache CacheManager for cache region '" + cacheName + "'");
            }
            this.cacheManager = CacheManager.getInstance();
        }
        synchronized (this.cacheManager) {
            boolean cacheExists = this.cacheManager.cacheExists(cacheName);
            if (cacheExists) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Using existing EhCache cache region '" + cacheName + "'");
                }
                rawCache = this.cacheManager.getEhcache(cacheName);
            } else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Creating new EhCache cache region '" + cacheName + "'");
                }
                rawCache = createCache();
                rawCache.setBootstrapCacheLoader(this.bootstrapCacheLoader);
            }
            if (this.cacheEventListeners != null) {
                for (CacheEventListener listener : this.cacheEventListeners) {
                    rawCache.getCacheEventNotificationService().registerListener(listener);
                }
            }
            if (!cacheExists) {
                this.cacheManager.addCache(rawCache);
            }
            if (this.disabled) {
                rawCache.setDisabled(true);
            }
            Ehcache decoratedCache = decorateCache(rawCache);
            if (decoratedCache != rawCache) {
                this.cacheManager.replaceCacheWithDecoratedCache(rawCache, decoratedCache);
            }
            this.cache = decoratedCache;
        }
    }

    protected Cache createCache() {
        return new Cache(this);
    }

    protected Ehcache decorateCache(Ehcache cache) {
        if (this.cacheEntryFactory != null) {
            if (this.cacheEntryFactory instanceof UpdatingCacheEntryFactory) {
                return new UpdatingSelfPopulatingCache(cache, this.cacheEntryFactory);
            }
            return new SelfPopulatingCache(cache, this.cacheEntryFactory);
        }
        if (this.blocking) {
            return new BlockingCache(cache);
        }
        return cache;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Ehcache getObject() {
        return this.cache;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends Ehcache> getObjectType() {
        if (this.cache != null) {
            return this.cache.getClass();
        }
        if (this.cacheEntryFactory != null) {
            if (this.cacheEntryFactory instanceof UpdatingCacheEntryFactory) {
                return UpdatingSelfPopulatingCache.class;
            }
            return SelfPopulatingCache.class;
        }
        if (this.blocking) {
            return BlockingCache.class;
        }
        return Cache.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
