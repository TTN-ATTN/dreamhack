package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cassandra/DriverConfigLoaderBuilderCustomizer.class */
public interface DriverConfigLoaderBuilderCustomizer {
    void customize(ProgrammaticDriverConfigLoaderBuilder builder);
}
