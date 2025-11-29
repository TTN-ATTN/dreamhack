package org.springframework.boot.autoconfigure.neo4j;

import org.neo4j.driver.Config;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/neo4j/ConfigBuilderCustomizer.class */
public interface ConfigBuilderCustomizer {
    void customize(Config.ConfigBuilder configBuilder);
}
