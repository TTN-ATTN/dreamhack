package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AliasRegistry;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/BeanDefinitionRegistry.class */
public interface BeanDefinitionRegistry extends AliasRegistry {
    void registerBeanDefinition(String str, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

    void removeBeanDefinition(String str) throws NoSuchBeanDefinitionException;

    BeanDefinition getBeanDefinition(String str) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String str);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean isBeanNameInUse(String str);
}
