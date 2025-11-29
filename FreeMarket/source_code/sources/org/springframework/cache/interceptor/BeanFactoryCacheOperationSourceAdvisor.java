package org.springframework.cache.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/BeanFactoryCacheOperationSourceAdvisor.class */
public class BeanFactoryCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    @Nullable
    private CacheOperationSource cacheOperationSource;
    private final CacheOperationSourcePointcut pointcut = new CacheOperationSourcePointcut() { // from class: org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor.1
        @Override // org.springframework.cache.interceptor.CacheOperationSourcePointcut
        @Nullable
        protected CacheOperationSource getCacheOperationSource() {
            return BeanFactoryCacheOperationSourceAdvisor.this.cacheOperationSource;
        }
    };

    public void setCacheOperationSource(CacheOperationSource cacheOperationSource) {
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
