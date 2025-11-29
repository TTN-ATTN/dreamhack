package org.springframework.cache.jcache;

import java.net.URI;
import java.util.Properties;
import javax.cache.CacheManager;
import javax.cache.Caching;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/JCacheManagerFactoryBean.class */
public class JCacheManagerFactoryBean implements FactoryBean<CacheManager>, BeanClassLoaderAware, InitializingBean, DisposableBean {

    @Nullable
    private URI cacheManagerUri;

    @Nullable
    private Properties cacheManagerProperties;

    @Nullable
    private ClassLoader beanClassLoader;

    @Nullable
    private CacheManager cacheManager;

    public void setCacheManagerUri(@Nullable URI cacheManagerUri) {
        this.cacheManagerUri = cacheManagerUri;
    }

    public void setCacheManagerProperties(@Nullable Properties cacheManagerProperties) {
        this.cacheManagerProperties = cacheManagerProperties;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.cacheManager = Caching.getCachingProvider().getCacheManager(this.cacheManagerUri, this.beanClassLoader, this.cacheManagerProperties);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public CacheManager getObject() {
        return this.cacheManager;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.cacheManager != null) {
            this.cacheManager.close();
        }
    }
}
