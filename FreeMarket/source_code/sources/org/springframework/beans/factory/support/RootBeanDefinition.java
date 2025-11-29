package org.springframework.beans.factory.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/RootBeanDefinition.class */
public class RootBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private BeanDefinitionHolder decoratedDefinition;

    @Nullable
    private AnnotatedElement qualifiedElement;
    volatile boolean stale;
    boolean allowCaching;
    boolean isFactoryMethodUnique;

    @Nullable
    volatile ResolvableType targetType;

    @Nullable
    volatile Class<?> resolvedTargetType;

    @Nullable
    volatile Boolean isFactoryBean;

    @Nullable
    volatile ResolvableType factoryMethodReturnType;

    @Nullable
    volatile Method factoryMethodToIntrospect;

    @Nullable
    volatile String resolvedDestroyMethodName;
    final Object constructorArgumentLock;

    @Nullable
    Executable resolvedConstructorOrFactoryMethod;
    boolean constructorArgumentsResolved;

    @Nullable
    Object[] resolvedConstructorArguments;

    @Nullable
    Object[] preparedConstructorArguments;
    final Object postProcessingLock;
    boolean postProcessed;

    @Nullable
    volatile Boolean beforeInstantiationResolved;

    @Nullable
    private Set<Member> externallyManagedConfigMembers;

    @Nullable
    private Set<String> externallyManagedInitMethods;

    @Nullable
    private Set<String> externallyManagedDestroyMethods;

    public RootBeanDefinition() {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
    }

    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
        setInstanceSupplier(instanceSupplier);
    }

    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
        setScope(scope);
        setInstanceSupplier(instanceSupplier);
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
        setAutowireMode(autowireMode);
        if (dependencyCheck && getResolvedAutowireMode() != 3) {
            setDependencyCheck(1);
        }
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClass(beanClass);
    }

    public RootBeanDefinition(String beanClassName) {
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.decoratedDefinition = original.decoratedDefinition;
        this.qualifiedElement = original.qualifiedElement;
        this.allowCaching = original.allowCaching;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
        this.targetType = original.targetType;
        this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
    }

    RootBeanDefinition(BeanDefinition original) {
        super(original);
        this.allowCaching = true;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public String getParentName() {
        return null;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setParentName(@Nullable String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }

    public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
        this.decoratedDefinition = decoratedDefinition;
    }

    @Nullable
    public BeanDefinitionHolder getDecoratedDefinition() {
        return this.decoratedDefinition;
    }

    public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
        this.qualifiedElement = qualifiedElement;
    }

    @Nullable
    public AnnotatedElement getQualifiedElement() {
        return this.qualifiedElement;
    }

    public void setTargetType(ResolvableType targetType) {
        this.targetType = targetType;
    }

    public void setTargetType(@Nullable Class<?> targetType) {
        this.targetType = targetType != null ? ResolvableType.forClass(targetType) : null;
    }

    @Nullable
    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        }
        ResolvableType targetType = this.targetType;
        if (targetType != null) {
            return targetType.resolve();
        }
        return null;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.factory.config.BeanDefinition
    public ResolvableType getResolvableType() {
        ResolvableType targetType = this.targetType;
        if (targetType != null) {
            return targetType;
        }
        ResolvableType returnType = this.factoryMethodReturnType;
        if (returnType != null) {
            return returnType;
        }
        Method factoryMethod = this.factoryMethodToIntrospect;
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return super.getResolvableType();
    }

    @Nullable
    public Constructor<?>[] getPreferredConstructors() {
        return null;
    }

    public void setUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }

    public void setNonUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = false;
    }

    public boolean isFactoryMethod(Method candidate) {
        return candidate.getName().equals(getFactoryMethodName());
    }

    public void setResolvedFactoryMethod(@Nullable Method method) {
        this.factoryMethodToIntrospect = method;
    }

    @Nullable
    public Method getResolvedFactoryMethod() {
        return this.factoryMethodToIntrospect;
    }

    public void registerExternallyManagedConfigMember(Member configMember) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedConfigMembers == null) {
                this.externallyManagedConfigMembers = new LinkedHashSet(1);
            }
            this.externallyManagedConfigMembers.add(configMember);
        }
    }

    public boolean isExternallyManagedConfigMember(Member configMember) {
        boolean z;
        synchronized (this.postProcessingLock) {
            z = this.externallyManagedConfigMembers != null && this.externallyManagedConfigMembers.contains(configMember);
        }
        return z;
    }

    public Set<Member> getExternallyManagedConfigMembers() {
        Set<Member> setEmptySet;
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedConfigMembers != null) {
                setEmptySet = Collections.unmodifiableSet(new LinkedHashSet(this.externallyManagedConfigMembers));
            } else {
                setEmptySet = Collections.emptySet();
            }
        }
        return setEmptySet;
    }

    public void registerExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedInitMethods == null) {
                this.externallyManagedInitMethods = new LinkedHashSet(1);
            }
            this.externallyManagedInitMethods.add(initMethod);
        }
    }

    public boolean isExternallyManagedInitMethod(String initMethod) {
        boolean z;
        synchronized (this.postProcessingLock) {
            z = this.externallyManagedInitMethods != null && this.externallyManagedInitMethods.contains(initMethod);
        }
        return z;
    }

    boolean hasAnyExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            if (isExternallyManagedInitMethod(initMethod)) {
                return true;
            }
            if (this.externallyManagedInitMethods != null) {
                for (String candidate : this.externallyManagedInitMethods) {
                    int indexOfDot = candidate.lastIndexOf(46);
                    if (indexOfDot >= 0) {
                        String methodName = candidate.substring(indexOfDot + 1);
                        if (methodName.equals(initMethod)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public Set<String> getExternallyManagedInitMethods() {
        Set<String> setEmptySet;
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedInitMethods != null) {
                setEmptySet = Collections.unmodifiableSet(new LinkedHashSet(this.externallyManagedInitMethods));
            } else {
                setEmptySet = Collections.emptySet();
            }
        }
        return setEmptySet;
    }

    public void registerExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedDestroyMethods == null) {
                this.externallyManagedDestroyMethods = new LinkedHashSet(1);
            }
            this.externallyManagedDestroyMethods.add(destroyMethod);
        }
    }

    public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
        boolean z;
        synchronized (this.postProcessingLock) {
            z = this.externallyManagedDestroyMethods != null && this.externallyManagedDestroyMethods.contains(destroyMethod);
        }
        return z;
    }

    boolean hasAnyExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized (this.postProcessingLock) {
            if (isExternallyManagedDestroyMethod(destroyMethod)) {
                return true;
            }
            if (this.externallyManagedDestroyMethods != null) {
                for (String candidate : this.externallyManagedDestroyMethods) {
                    int indexOfDot = candidate.lastIndexOf(46);
                    if (indexOfDot >= 0) {
                        String methodName = candidate.substring(indexOfDot + 1);
                        if (methodName.equals(destroyMethod)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public Set<String> getExternallyManagedDestroyMethods() {
        Set<String> setEmptySet;
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedDestroyMethods != null) {
                setEmptySet = Collections.unmodifiableSet(new LinkedHashSet(this.externallyManagedDestroyMethods));
            } else {
                setEmptySet = Collections.emptySet();
            }
        }
        return setEmptySet;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.core.AttributeAccessorSupport
    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof RootBeanDefinition) && super.equals(other));
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanDefinition
    public String toString() {
        return "Root bean: " + super.toString();
    }
}
