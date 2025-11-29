package org.springframework.aop.framework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/framework/AbstractAdvisingBeanPostProcessor.class */
public abstract class AbstractAdvisingBeanPostProcessor extends ProxyProcessorSupport implements BeanPostProcessor {

    @Nullable
    protected Advisor advisor;
    protected boolean beforeExistingAdvisors = false;
    private final Map<Class<?>, Boolean> eligibleBeans = new ConcurrentHashMap(256);

    public void setBeforeExistingAdvisors(boolean beforeExistingAdvisors) {
        this.beforeExistingAdvisors = beforeExistingAdvisors;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v0 */
    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object obj, String str) throws AopConfigException {
        if (this.advisor == null || (obj instanceof AopInfrastructureBean)) {
            return obj;
        }
        if (obj instanceof Advised) {
            Advised advised = (Advised) obj;
            if (!advised.isFrozen() && isEligible(AopUtils.getTargetClass(obj))) {
                if (this.beforeExistingAdvisors) {
                    advised.addAdvisor(0, this.advisor);
                } else {
                    advised.addAdvisor(this.advisor);
                }
                return obj;
            }
        }
        if (isEligible(obj, str)) {
            ProxyFactory proxyFactoryPrepareProxyFactory = prepareProxyFactory(obj, str);
            if (!proxyFactoryPrepareProxyFactory.isProxyTargetClass()) {
                evaluateProxyInterfaces(obj.getClass(), proxyFactoryPrepareProxyFactory);
            }
            proxyFactoryPrepareProxyFactory.addAdvisor(this.advisor);
            customizeProxyFactory(proxyFactoryPrepareProxyFactory);
            ?? proxyClassLoader = getProxyClassLoader();
            boolean z = proxyClassLoader instanceof SmartClassLoader;
            ClassLoader originalClassLoader = proxyClassLoader;
            if (z) {
                originalClassLoader = proxyClassLoader;
                if (proxyClassLoader != obj.getClass().getClassLoader()) {
                    originalClassLoader = ((SmartClassLoader) proxyClassLoader).getOriginalClassLoader();
                }
            }
            return proxyFactoryPrepareProxyFactory.getProxy(originalClassLoader);
        }
        return obj;
    }

    protected boolean isEligible(Object bean, String beanName) {
        return isEligible(bean.getClass());
    }

    protected boolean isEligible(Class<?> targetClass) {
        Boolean eligible = this.eligibleBeans.get(targetClass);
        if (eligible != null) {
            return eligible.booleanValue();
        }
        if (this.advisor == null) {
            return false;
        }
        Boolean eligible2 = Boolean.valueOf(AopUtils.canApply(this.advisor, targetClass));
        this.eligibleBeans.put(targetClass, eligible2);
        return eligible2.booleanValue();
    }

    protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.copyFrom(this);
        proxyFactory.setTarget(bean);
        return proxyFactory;
    }

    protected void customizeProxyFactory(ProxyFactory proxyFactory) {
    }
}
