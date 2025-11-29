package org.springframework.boot.context.properties.bind;

import java.lang.reflect.Constructor;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/BindConstructorProvider.class */
public interface BindConstructorProvider {
    public static final BindConstructorProvider DEFAULT = new DefaultBindConstructorProvider();

    Constructor<?> getBindConstructor(Bindable<?> bindable, boolean isNestedConstructorBinding);
}
