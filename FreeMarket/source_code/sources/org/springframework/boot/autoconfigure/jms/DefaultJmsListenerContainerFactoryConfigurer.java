package org.springframework.boot.autoconfigure.jms;

import java.time.Duration;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/DefaultJmsListenerContainerFactoryConfigurer.class */
public final class DefaultJmsListenerContainerFactoryConfigurer {
    private DestinationResolver destinationResolver;
    private MessageConverter messageConverter;
    private ExceptionListener exceptionListener;
    private JtaTransactionManager transactionManager;
    private JmsProperties jmsProperties;

    void setDestinationResolver(DestinationResolver destinationResolver) {
        this.destinationResolver = destinationResolver;
    }

    void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    void setTransactionManager(JtaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    void setJmsProperties(JmsProperties jmsProperties) {
        this.jmsProperties = jmsProperties;
    }

    public void configure(DefaultJmsListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        Assert.notNull(factory, "Factory must not be null");
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(Boolean.valueOf(this.jmsProperties.isPubSubDomain()));
        if (this.transactionManager != null) {
            factory.setTransactionManager(this.transactionManager);
        } else {
            factory.setSessionTransacted(true);
        }
        if (this.destinationResolver != null) {
            factory.setDestinationResolver(this.destinationResolver);
        }
        if (this.messageConverter != null) {
            factory.setMessageConverter(this.messageConverter);
        }
        if (this.exceptionListener != null) {
            factory.setExceptionListener(this.exceptionListener);
        }
        JmsProperties.Listener listener = this.jmsProperties.getListener();
        factory.setAutoStartup(listener.isAutoStartup());
        if (listener.getAcknowledgeMode() != null) {
            factory.setSessionAcknowledgeMode(Integer.valueOf(listener.getAcknowledgeMode().getMode()));
        }
        String concurrency = listener.formatConcurrency();
        if (concurrency != null) {
            factory.setConcurrency(concurrency);
        }
        Duration receiveTimeout = listener.getReceiveTimeout();
        if (receiveTimeout != null) {
            factory.setReceiveTimeout(Long.valueOf(receiveTimeout.toMillis()));
        }
    }
}
