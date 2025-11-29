package org.springframework.boot.autoconfigure.couchbase;

import com.couchbase.client.java.env.ClusterEnvironment;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/couchbase/ClusterEnvironmentBuilderCustomizer.class */
public interface ClusterEnvironmentBuilderCustomizer {
    void customize(ClusterEnvironment.Builder builder);
}
