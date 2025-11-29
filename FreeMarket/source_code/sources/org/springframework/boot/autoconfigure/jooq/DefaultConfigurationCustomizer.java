package org.springframework.boot.autoconfigure.jooq;

import org.jooq.impl.DefaultConfiguration;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jooq/DefaultConfigurationCustomizer.class */
public interface DefaultConfigurationCustomizer {
    void customize(DefaultConfiguration configuration);
}
