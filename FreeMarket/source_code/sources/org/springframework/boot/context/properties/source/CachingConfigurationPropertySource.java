package org.springframework.boot.context.properties.source;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/source/CachingConfigurationPropertySource.class */
interface CachingConfigurationPropertySource {
    ConfigurationPropertyCaching getCaching();

    static ConfigurationPropertyCaching find(ConfigurationPropertySource source) {
        if (source instanceof CachingConfigurationPropertySource) {
            return ((CachingConfigurationPropertySource) source).getCaching();
        }
        return null;
    }
}
