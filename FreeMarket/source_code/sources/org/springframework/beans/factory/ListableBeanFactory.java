package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/ListableBeanFactory.class */
public interface ListableBeanFactory extends BeanFactory {
    boolean containsBeanDefinition(String str);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    <T> ObjectProvider<T> getBeanProvider(Class<T> cls, boolean z);

    <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType, boolean z);

    String[] getBeanNamesForType(ResolvableType resolvableType);

    String[] getBeanNamesForType(ResolvableType resolvableType, boolean z, boolean z2);

    String[] getBeanNamesForType(@Nullable Class<?> cls);

    String[] getBeanNamesForType(@Nullable Class<?> cls, boolean z, boolean z2);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> cls) throws BeansException;

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> cls, boolean z, boolean z2) throws BeansException;

    String[] getBeanNamesForAnnotation(Class<? extends Annotation> cls);

    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> cls) throws BeansException;

    @Nullable
    <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls) throws NoSuchBeanDefinitionException;

    @Nullable
    <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls, boolean z) throws NoSuchBeanDefinitionException;
}
