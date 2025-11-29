package org.springframework.boot.autoconfigure.jms.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryCustomizer.class */
public interface ActiveMQConnectionFactoryCustomizer {
    void customize(ActiveMQConnectionFactory factory);
}
