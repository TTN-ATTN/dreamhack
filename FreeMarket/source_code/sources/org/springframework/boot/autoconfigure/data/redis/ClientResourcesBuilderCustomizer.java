package org.springframework.boot.autoconfigure.data.redis;

import io.lettuce.core.resource.ClientResources;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/redis/ClientResourcesBuilderCustomizer.class */
public interface ClientResourcesBuilderCustomizer {
    void customize(ClientResources.Builder clientResourcesBuilder);
}
