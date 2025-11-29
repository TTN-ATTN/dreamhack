package org.springframework.boot.context.properties;

import org.springframework.boot.context.properties.bind.BindHandler;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindHandlerAdvisor.class */
public interface ConfigurationPropertiesBindHandlerAdvisor {
    BindHandler apply(BindHandler bindHandler);
}
