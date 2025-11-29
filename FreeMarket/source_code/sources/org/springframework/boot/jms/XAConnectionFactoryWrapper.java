package org.springframework.boot.jms;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jms/XAConnectionFactoryWrapper.class */
public interface XAConnectionFactoryWrapper {
    ConnectionFactory wrapConnectionFactory(XAConnectionFactory connectionFactory) throws Exception;
}
