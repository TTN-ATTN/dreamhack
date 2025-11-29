package org.springframework.cache.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/ehcache/EhCacheManagerFactoryBean.class */
public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager>, InitializingBean, DisposableBean {

    @Nullable
    private Resource configLocation;

    @Nullable
    private String cacheManagerName;

    @Nullable
    private CacheManager cacheManager;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean acceptExisting = false;
    private boolean shared = false;
    private boolean locallyManaged = true;

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    public void setCacheManagerName(String cacheManagerName) {
        this.cacheManagerName = cacheManagerName;
    }

    public void setAcceptExisting(boolean acceptExisting) {
        this.acceptExisting = acceptExisting;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws CacheException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Initializing EhCache CacheManager" + (this.cacheManagerName != null ? " '" + this.cacheManagerName + "'" : ""));
        }
        Configuration configuration = this.configLocation != null ? EhCacheManagerUtils.parseConfiguration(this.configLocation) : ConfigurationFactory.parseConfiguration();
        if (this.cacheManagerName != null) {
            configuration.setName(this.cacheManagerName);
        }
        if (this.shared) {
            this.cacheManager = CacheManager.create(configuration);
            return;
        }
        if (this.acceptExisting) {
            synchronized (CacheManager.class) {
                this.cacheManager = CacheManager.getCacheManager(this.cacheManagerName);
                if (this.cacheManager == null) {
                    this.cacheManager = new CacheManager(configuration);
                } else {
                    this.locallyManaged = false;
                }
            }
            return;
        }
        this.cacheManager = new CacheManager(configuration);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public CacheManager getObject() {
        return this.cacheManager;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends CacheManager> getObjectType() {
        return this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.cacheManager != null && this.locallyManaged) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Shutting down EhCache CacheManager" + (this.cacheManagerName != null ? " '" + this.cacheManagerName + "'" : ""));
            }
            this.cacheManager.shutdown();
        }
    }
}
