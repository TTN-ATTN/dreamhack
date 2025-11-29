package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.client.ConnectionFactory;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/ConnectionFactoryCustomizer.class */
public interface ConnectionFactoryCustomizer {
    void customize(ConnectionFactory factory);
}
