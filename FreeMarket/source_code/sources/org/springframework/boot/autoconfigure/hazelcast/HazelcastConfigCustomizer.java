package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.config.Config;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastConfigCustomizer.class */
public interface HazelcastConfigCustomizer {
    void customize(Config config);
}
