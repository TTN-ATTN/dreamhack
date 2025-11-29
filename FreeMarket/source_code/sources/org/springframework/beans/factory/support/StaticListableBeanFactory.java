package org.springframework.beans.factory.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/StaticListableBeanFactory.class */
public class StaticListableBeanFactory implements ListableBeanFactory {
    private final Map<String, Object> beans;

    public StaticListableBeanFactory() {
        this.beans = new LinkedHashMap();
    }

    public StaticListableBeanFactory(Map<String, Object> beans) {
        Assert.notNull(beans, "Beans Map must not be null");
        this.beans = beans;
    }

    public void addBean(String name, Object bean) {
        this.beans.put(name, bean);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name) throws BeansException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (BeanFactoryUtils.isFactoryDereference(name) && !(bean instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, bean.getClass());
        }
        if ((bean instanceof FactoryBean) && !BeanFactoryUtils.isFactoryDereference(name)) {
            try {
                Object exposedObject = ((FactoryBean) bean).getObject();
                if (exposedObject == null) {
                    throw new BeanCreationException(beanName, "FactoryBean exposed null object");
                }
                return exposedObject;
            } catch (Exception ex) {
                throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
            }
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(String str, @Nullable Class<T> cls) throws BeansException {
        T t = (T) getBean(str);
        if (cls != null && !cls.isInstance(t)) {
            throw new BeanNotOfRequiredTypeException(str, cls, t.getClass());
        }
        return t;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name, Object... args) throws BeansException {
        if (!ObjectUtils.isEmpty(args)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return getBean(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls) throws BeansException {
        String[] beanNamesForType = getBeanNamesForType((Class<?>) cls);
        if (beanNamesForType.length == 1) {
            return (T) getBean(beanNamesForType[0], cls);
        }
        if (beanNamesForType.length > 1) {
            throw new NoUniqueBeanDefinitionException((Class<?>) cls, beanNamesForType);
        }
        throw new NoSuchBeanDefinitionException((Class<?>) cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls, Object... objArr) throws BeansException {
        if (!ObjectUtils.isEmpty(objArr)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return (T) getBean(cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeansException {
        return getBeanProvider(ResolvableType.forRawClass(requiredType), true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return getBeanProvider(requiredType, true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean containsBean(String name) {
        return this.beans.containsKey(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isSingleton(String name) throws BeansException {
        Object bean = getBean(name);
        if (bean instanceof FactoryBean) {
            return ((FactoryBean) bean).isSingleton();
        }
        return true;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isPrototype(String name) throws BeansException {
        Object bean = getBean(name);
        return ((bean instanceof SmartFactoryBean) && ((SmartFactoryBean) bean).isPrototype()) || ((bean instanceof FactoryBean) && !((FactoryBean) bean).isSingleton());
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return typeToMatch == null || (type != null && typeToMatch.isAssignableFrom(type));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getType(name, true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if ((bean instanceof FactoryBean) && !BeanFactoryUtils.isFactoryDereference(name)) {
            return ((FactoryBean) bean).getObjectType();
        }
        return bean.getClass();
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public String[] getAliases(String name) {
        return new String[0];
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean containsBeanDefinition(String name) {
        return this.beans.containsKey(name);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public int getBeanDefinitionCount() {
        return this.beans.size();
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beans.keySet());
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return getBeanProvider(ResolvableType.forRawClass(requiredType), allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, boolean allowEagerInit) {
        return new ObjectProvider<T>() { // from class: org.springframework.beans.factory.support.StaticListableBeanFactory.1
            @Override // org.springframework.beans.factory.ObjectFactory
            public T getObject() throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0], requiredType);
                }
                if (beanNamesForType.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNamesForType);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public T getObject(Object... objArr) throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0], objArr);
                }
                if (beanNamesForType.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNamesForType);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfAvailable() throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0]);
                }
                if (beanNamesForType.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNamesForType);
                }
                return null;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfUnique() throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0]);
                }
                return null;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> stream() {
                return Arrays.stream(StaticListableBeanFactory.this.getBeanNamesForType(requiredType)).map(name -> {
                    return StaticListableBeanFactory.this.getBean(name);
                });
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> orderedStream() {
                return stream().sorted(OrderComparator.INSTANCE);
            }
        };
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable ResolvableType type) {
        return getBeanNamesForType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        Class<?> resolved = type != null ? type.resolve() : null;
        boolean isFactoryType = resolved != null && FactoryBean.class.isAssignableFrom(resolved);
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if ((beanInstance instanceof FactoryBean) && !isFactoryType) {
                FactoryBean<?> factoryBean = (FactoryBean) beanInstance;
                Class<?> objectType = factoryBean.getObjectType();
                if (includeNonSingletons || factoryBean.isSingleton()) {
                    if (objectType != null && (type == null || type.isAssignableFrom(objectType))) {
                        matches.add(beanName);
                    }
                }
            } else if (type == null || type.isInstance(beanInstance)) {
                matches.add(beanName);
            }
        }
        return StringUtils.toStringArray(matches);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        return getBeanNamesForType(ResolvableType.forClass(type));
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return getBeanNamesForType(ResolvableType.forClass(type), includeNonSingletons, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return getBeansOfType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        boolean isFactoryType = type != null && FactoryBean.class.isAssignableFrom(type);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if ((beanInstance instanceof FactoryBean) && !isFactoryType) {
                FactoryBean<?> factory = (FactoryBean) beanInstance;
                Class<?> objectType = factory.getObjectType();
                if (includeNonSingletons || factory.isSingleton()) {
                    if (objectType != null && (type == null || type.isAssignableFrom(objectType))) {
                        linkedHashMap.put(beanName, getBean(beanName, type));
                    }
                }
            } else if (type == null || type.isInstance(beanInstance)) {
                if (isFactoryType) {
                    beanName = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
                }
                linkedHashMap.put(beanName, beanInstance);
            }
        }
        return linkedHashMap;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        List<String> results = new ArrayList<>();
        for (String beanName : this.beans.keySet()) {
            if (findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }
        return StringUtils.toStringArray(results);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        Map<String, Object> results = new LinkedHashMap<>();
        for (String beanName : this.beans.keySet()) {
            if (findAnnotationOnBean(beanName, annotationType) != null) {
                results.put(beanName, getBean(beanName));
            }
        }
        return results;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls) throws NoSuchBeanDefinitionException {
        return (A) findAnnotationOnBean(str, cls, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls, boolean z) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(str, z);
        if (type != null) {
            return (A) AnnotatedElementUtils.findMergedAnnotation(type, cls);
        }
        return null;
    }
}
