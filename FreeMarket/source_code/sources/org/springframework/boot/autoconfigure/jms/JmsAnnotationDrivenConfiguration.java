package org.springframework.boot.autoconfigure.jms;

import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({EnableJms.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration.class */
class JmsAnnotationDrivenConfiguration {
    private final ObjectProvider<DestinationResolver> destinationResolver;
    private final ObjectProvider<JtaTransactionManager> transactionManager;
    private final ObjectProvider<MessageConverter> messageConverter;
    private final ObjectProvider<ExceptionListener> exceptionListener;
    private final JmsProperties properties;

    JmsAnnotationDrivenConfiguration(ObjectProvider<DestinationResolver> destinationResolver, ObjectProvider<JtaTransactionManager> transactionManager, ObjectProvider<MessageConverter> messageConverter, ObjectProvider<ExceptionListener> exceptionListener, JmsProperties properties) {
        this.destinationResolver = destinationResolver;
        this.transactionManager = transactionManager;
        this.messageConverter = messageConverter;
        this.exceptionListener = exceptionListener;
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    DefaultJmsListenerContainerFactoryConfigurer jmsListenerContainerFactoryConfigurer() {
        DefaultJmsListenerContainerFactoryConfigurer configurer = new DefaultJmsListenerContainerFactoryConfigurer();
        configurer.setDestinationResolver(this.destinationResolver.getIfUnique());
        configurer.setTransactionManager(this.transactionManager.getIfUnique());
        configurer.setMessageConverter(this.messageConverter.getIfUnique());
        configurer.setExceptionListener(this.exceptionListener.getIfUnique());
        configurer.setJmsProperties(this.properties);
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"jmsListenerContainerFactory"})
    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    DefaultJmsListenerContainerFactory jmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @ConditionalOnMissingBean(name = {"org.springframework.jms.config.internalJmsListenerAnnotationProcessor"})
    @Configuration(proxyBeanMethods = false)
    @EnableJms
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration$EnableJmsConfiguration.class */
    static class EnableJmsConfiguration {
        EnableJmsConfiguration() {
        }
    }

    @ConditionalOnJndi
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration$JndiConfiguration.class */
    static class JndiConfiguration {
        JndiConfiguration() {
        }

        @ConditionalOnMissingBean({DestinationResolver.class})
        @Bean
        JndiDestinationResolver destinationResolver() {
            JndiDestinationResolver resolver = new JndiDestinationResolver();
            resolver.setFallbackToDynamicDestination(true);
            return resolver;
        }
    }
}
