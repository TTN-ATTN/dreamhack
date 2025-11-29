package org.springframework.cache.jcache.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/cache/jcache/interceptor/BeanFactoryJCacheOperationSourceAdvisor.class */
public class BeanFactoryJCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    @Nullable
    private JCacheOperationSource cacheOperationSource;
    private final JCacheOperationSourcePointcut pointcut = new JCacheOperationSourcePointcut() { // from class: org.springframework.cache.jcache.interceptor.BeanFactoryJCacheOperationSourceAdvisor.1
        @Override // org.springframework.cache.jcache.interceptor.JCacheOperationSourcePointcut
        protected JCacheOperationSource getCacheOperationSource() {
            return BeanFactoryJCacheOperationSourceAdvisor.this.cacheOperationSource;
        }
    };

    public void setCacheOperationSource(JCacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
