package org.springframework.boot.autoconfigure.jms.activemq;

import java.util.List;
import java.util.stream.Collectors;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.catalina.Lifecycle;
import org.apache.commons.pool2.PooledObject;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryFactory;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

@ConditionalOnMissingBean({ConnectionFactory.class})
@Configuration(proxyBeanMethods = false)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryConfiguration.class */
class ActiveMQConnectionFactoryConfiguration {
    ActiveMQConnectionFactoryConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.activemq.pool", name = {"enabled"}, havingValue = "false", matchIfMissing = true)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryConfiguration$SimpleConnectionFactoryConfiguration.class */
    static class SimpleConnectionFactoryConfiguration {
        SimpleConnectionFactoryConfiguration() {
        }

        @ConditionalOnProperty(prefix = "spring.jms.cache", name = {"enabled"}, havingValue = "false")
        @Bean
        ActiveMQConnectionFactory jmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers) {
            return createJmsConnectionFactory(properties, factoryCustomizers);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static ActiveMQConnectionFactory createJmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers) {
            return new ActiveMQConnectionFactoryFactory(properties, (List) factoryCustomizers.orderedStream().collect(Collectors.toList())).createConnectionFactory(ActiveMQConnectionFactory.class);
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({CachingConnectionFactory.class})
        @ConditionalOnProperty(prefix = "spring.jms.cache", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryConfiguration$SimpleConnectionFactoryConfiguration$CachingConnectionFactoryConfiguration.class */
        static class CachingConnectionFactoryConfiguration {
            CachingConnectionFactoryConfiguration() {
            }

            @Bean
            CachingConnectionFactory jmsConnectionFactory(JmsProperties jmsProperties, ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers) {
                JmsProperties.Cache cacheProperties = jmsProperties.getCache();
                CachingConnectionFactory connectionFactory = new CachingConnectionFactory(SimpleConnectionFactoryConfiguration.createJmsConnectionFactory(properties, factoryCustomizers));
                connectionFactory.setCacheConsumers(cacheProperties.isConsumers());
                connectionFactory.setCacheProducers(cacheProperties.isProducers());
                connectionFactory.setSessionCacheSize(cacheProperties.getSessionCacheSize());
                return connectionFactory;
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JmsPoolConnectionFactory.class, PooledObject.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryConfiguration$PooledConnectionFactoryConfiguration.class */
    static class PooledConnectionFactoryConfiguration {
        PooledConnectionFactoryConfiguration() {
        }

        @ConditionalOnProperty(prefix = "spring.activemq.pool", name = {"enabled"}, havingValue = "true")
        @Bean(destroyMethod = Lifecycle.STOP_EVENT)
        JmsPoolConnectionFactory jmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers) {
            return new JmsPoolConnectionFactoryFactory(properties.getPool()).createPooledConnectionFactory(new ActiveMQConnectionFactoryFactory(properties, (List) factoryCustomizers.orderedStream().collect(Collectors.toList())).createConnectionFactory(ActiveMQConnectionFactory.class));
        }
    }
}
