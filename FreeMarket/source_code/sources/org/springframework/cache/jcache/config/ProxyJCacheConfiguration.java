package org.springframework.cache.jcache.config;

import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.cache.jcache.interceptor.BeanFactoryJCacheOperationSourceAdvisor;
import org.springframework.cache.jcache.interceptor.JCacheInterceptor;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration(proxyBeanMethods = false)
@Role(2)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/config/ProxyJCacheConfiguration.class */
public class ProxyJCacheConfiguration extends AbstractJCacheConfiguration {
    @Bean(name = {CacheManagementConfigUtils.JCACHE_ADVISOR_BEAN_NAME})
    @Role(2)
    public BeanFactoryJCacheOperationSourceAdvisor cacheAdvisor(JCacheOperationSource jCacheOperationSource, JCacheInterceptor jCacheInterceptor) {
        BeanFactoryJCacheOperationSourceAdvisor advisor = new BeanFactoryJCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(jCacheOperationSource);
        advisor.setAdvice(jCacheInterceptor);
        if (this.enableCaching != null) {
            advisor.setOrder(((Integer) this.enableCaching.getNumber("order")).intValue());
        }
        return advisor;
    }

    @Bean(name = {"jCacheInterceptor"})
    @Role(2)
    public JCacheInterceptor cacheInterceptor(JCacheOperationSource jCacheOperationSource) {
        JCacheInterceptor interceptor = new JCacheInterceptor(this.errorHandler);
        interceptor.setCacheOperationSource(jCacheOperationSource);
        return interceptor;
    }
}
