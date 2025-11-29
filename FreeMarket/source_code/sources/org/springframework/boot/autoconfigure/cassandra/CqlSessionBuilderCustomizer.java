package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.CqlSessionBuilder;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cassandra/CqlSessionBuilderCustomizer.class */
public interface CqlSessionBuilderCustomizer {
    void customize(CqlSessionBuilder cqlSessionBuilder);
}
