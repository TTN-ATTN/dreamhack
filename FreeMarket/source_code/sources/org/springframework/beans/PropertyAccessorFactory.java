package org.springframework.beans;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/PropertyAccessorFactory.class */
public final class PropertyAccessorFactory {
    private PropertyAccessorFactory() {
    }

    public static BeanWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

    public static ConfigurablePropertyAccessor forDirectFieldAccess(Object target) {
        return new DirectFieldAccessor(target);
    }
}
