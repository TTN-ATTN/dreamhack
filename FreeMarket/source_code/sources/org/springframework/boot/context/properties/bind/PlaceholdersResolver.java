package org.springframework.boot.context.properties.bind;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/PlaceholdersResolver.class */
public interface PlaceholdersResolver {
    public static final PlaceholdersResolver NONE = value -> {
        return value;
    };

    Object resolvePlaceholders(Object value);
}
