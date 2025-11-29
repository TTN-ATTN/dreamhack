package org.springframework.boot.context.properties.bind;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/DataObjectPropertyBinder.class */
interface DataObjectPropertyBinder {
    Object bindProperty(String propertyName, Bindable<?> target);
}
