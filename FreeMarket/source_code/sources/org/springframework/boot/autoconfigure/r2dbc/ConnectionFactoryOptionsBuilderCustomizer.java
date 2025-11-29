package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactoryOptions;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryOptionsBuilderCustomizer.class */
public interface ConnectionFactoryOptionsBuilderCustomizer {
    void customize(ConnectionFactoryOptions.Builder builder);
}
