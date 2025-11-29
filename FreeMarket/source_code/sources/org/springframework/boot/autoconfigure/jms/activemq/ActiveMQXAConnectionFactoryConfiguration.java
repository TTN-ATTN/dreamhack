package org.springframework.boot.autoconfigure.jms.activemq;

import java.util.List;
import java.util.stream.Collectors;
import javax.jms.ConnectionFactory;
import javax.transaction.TransactionManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({TransactionManager.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@ConditionalOnBean({XAConnectionFactoryWrapper.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQXAConnectionFactoryConfiguration.class */
class ActiveMQXAConnectionFactoryConfiguration {
    ActiveMQXAConnectionFactoryConfiguration() {
    }

    @Primary
    @Bean(name = {"jmsConnectionFactory", "xaJmsConnectionFactory"})
    ConnectionFactory jmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers, XAConnectionFactoryWrapper wrapper) throws Exception {
        ActiveMQXAConnectionFactory connectionFactory = new ActiveMQConnectionFactoryFactory(properties, (List) factoryCustomizers.orderedStream().collect(Collectors.toList())).createConnectionFactory(ActiveMQXAConnectionFactory.class);
        return wrapper.wrapConnectionFactory(connectionFactory);
    }

    @ConditionalOnProperty(prefix = "spring.activemq.pool", name = {"enabled"}, havingValue = "false", matchIfMissing = true)
    @Bean
    ActiveMQConnectionFactory nonXaJmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers) {
        return new ActiveMQConnectionFactoryFactory(properties, (List) factoryCustomizers.orderedStream().collect(Collectors.toList())).createConnectionFactory(ActiveMQConnectionFactory.class);
    }
}
