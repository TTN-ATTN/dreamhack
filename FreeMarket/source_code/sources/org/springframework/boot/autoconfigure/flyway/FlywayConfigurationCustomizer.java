package org.springframework.boot.autoconfigure.flyway;

import org.flywaydb.core.api.configuration.FluentConfiguration;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayConfigurationCustomizer.class */
public interface FlywayConfigurationCustomizer {
    void customize(FluentConfiguration configuration);
}
