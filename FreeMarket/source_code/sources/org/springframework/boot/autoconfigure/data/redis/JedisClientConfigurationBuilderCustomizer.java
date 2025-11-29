package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/redis/JedisClientConfigurationBuilderCustomizer.class */
public interface JedisClientConfigurationBuilderCustomizer {
    void customize(JedisClientConfiguration.JedisClientConfigurationBuilder clientConfigurationBuilder);
}
