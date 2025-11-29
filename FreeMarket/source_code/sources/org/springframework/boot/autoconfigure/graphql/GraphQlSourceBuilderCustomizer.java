package org.springframework.boot.autoconfigure.graphql;

import org.springframework.graphql.execution.GraphQlSource;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/graphql/GraphQlSourceBuilderCustomizer.class */
public interface GraphQlSourceBuilderCustomizer {
    void customize(GraphQlSource.SchemaResourceBuilder builder);
}
