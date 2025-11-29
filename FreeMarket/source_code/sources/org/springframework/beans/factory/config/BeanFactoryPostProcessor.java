package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/config/BeanFactoryPostProcessor.class */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException;
}
