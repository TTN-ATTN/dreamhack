package org.springframework.boot.autoconfigure.amqp;

import java.util.List;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/AbstractRabbitListenerContainerFactoryConfigurer.class */
public abstract class AbstractRabbitListenerContainerFactoryConfigurer<T extends AbstractRabbitListenerContainerFactory<?>> {
    private MessageConverter messageConverter;
    private MessageRecoverer messageRecoverer;
    private List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;
    private RabbitProperties rabbitProperties;

    public abstract void configure(T factory, ConnectionFactory connectionFactory);

    @Deprecated
    protected AbstractRabbitListenerContainerFactoryConfigurer() {
    }

    protected AbstractRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    protected void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    protected void setMessageRecoverer(MessageRecoverer messageRecoverer) {
        this.messageRecoverer = messageRecoverer;
    }

    protected void setRetryTemplateCustomizers(List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        this.retryTemplateCustomizers = retryTemplateCustomizers;
    }

    @Deprecated
    protected void setRabbitProperties(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    protected final RabbitProperties getRabbitProperties() {
        return this.rabbitProperties;
    }

    protected void configure(T factory, ConnectionFactory connectionFactory, RabbitProperties.AmqpContainer configuration) {
        Assert.notNull(factory, "Factory must not be null");
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
        Assert.notNull(configuration, "Configuration must not be null");
        factory.setConnectionFactory(connectionFactory);
        if (this.messageConverter != null) {
            factory.setMessageConverter(this.messageConverter);
        }
        factory.setAutoStartup(Boolean.valueOf(configuration.isAutoStartup()));
        if (configuration.getAcknowledgeMode() != null) {
            factory.setAcknowledgeMode(configuration.getAcknowledgeMode());
        }
        if (configuration.getPrefetch() != null) {
            factory.setPrefetchCount(configuration.getPrefetch());
        }
        if (configuration.getDefaultRequeueRejected() != null) {
            factory.setDefaultRequeueRejected(configuration.getDefaultRequeueRejected());
        }
        if (configuration.getIdleEventInterval() != null) {
            factory.setIdleEventInterval(Long.valueOf(configuration.getIdleEventInterval().toMillis()));
        }
        factory.setMissingQueuesFatal(Boolean.valueOf(configuration.isMissingQueuesFatal()));
        factory.setDeBatchingEnabled(Boolean.valueOf(configuration.isDeBatchingEnabled()));
        RabbitProperties.ListenerRetry retryConfig = configuration.getRetry();
        if (retryConfig.isEnabled()) {
            RetryInterceptorBuilder.StatelessRetryInterceptorBuilder statelessRetryInterceptorBuilderStateless = retryConfig.isStateless() ? RetryInterceptorBuilder.stateless() : RetryInterceptorBuilder.stateful();
            RetryTemplate retryTemplate = new RetryTemplateFactory(this.retryTemplateCustomizers).createRetryTemplate(retryConfig, RabbitRetryTemplateCustomizer.Target.LISTENER);
            statelessRetryInterceptorBuilderStateless.retryOperations(retryTemplate);
            MessageRecoverer recoverer = this.messageRecoverer != null ? this.messageRecoverer : new RejectAndDontRequeueRecoverer();
            statelessRetryInterceptorBuilderStateless.recoverer(recoverer);
            factory.setAdviceChain(new Advice[]{statelessRetryInterceptorBuilderStateless.build()});
        }
    }
}
