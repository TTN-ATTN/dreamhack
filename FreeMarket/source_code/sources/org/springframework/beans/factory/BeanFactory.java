package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/BeanFactory.class */
public interface BeanFactory {
    public static final String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String str) throws BeansException;

    <T> T getBean(String str, Class<T> cls) throws BeansException;

    Object getBean(String str, Object... objArr) throws BeansException;

    <T> T getBean(Class<T> cls) throws BeansException;

    <T> T getBean(Class<T> cls, Object... objArr) throws BeansException;

    <T> ObjectProvider<T> getBeanProvider(Class<T> cls);

    <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType);

    boolean containsBean(String str);

    boolean isSingleton(String str) throws NoSuchBeanDefinitionException;

    boolean isPrototype(String str) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String str, ResolvableType resolvableType) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String str, Class<?> cls) throws NoSuchBeanDefinitionException;

    @Nullable
    Class<?> getType(String str) throws NoSuchBeanDefinitionException;

    @Nullable
    Class<?> getType(String str, boolean z) throws NoSuchBeanDefinitionException;

    String[] getAliases(String str);
}
