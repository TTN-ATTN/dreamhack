package org.springframework.boot.autoconfigure.kafka;

import org.springframework.kafka.config.StreamsBuilderFactoryBean;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/kafka/StreamsBuilderFactoryBeanCustomizer.class */
public interface StreamsBuilderFactoryBeanCustomizer {
    void customize(StreamsBuilderFactoryBean factoryBean);
}
