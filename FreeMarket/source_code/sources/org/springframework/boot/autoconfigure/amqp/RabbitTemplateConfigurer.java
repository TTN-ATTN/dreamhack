package org.springframework.boot.autoconfigure.amqp;

import java.util.List;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/RabbitTemplateConfigurer.class */
public class RabbitTemplateConfigurer {
    private MessageConverter messageConverter;
    private List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;
    private RabbitProperties rabbitProperties;

    @Deprecated
    public RabbitTemplateConfigurer() {
    }

    public RabbitTemplateConfigurer(RabbitProperties rabbitProperties) {
        Assert.notNull(rabbitProperties, "RabbitProperties must not be null");
        this.rabbitProperties = rabbitProperties;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void setRetryTemplateCustomizers(List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        this.retryTemplateCustomizers = retryTemplateCustomizers;
    }

    @Deprecated
    protected void setRabbitProperties(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    protected final RabbitProperties getRabbitProperties() {
        return this.rabbitProperties;
    }

    public void configure(RabbitTemplate template, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        template.setConnectionFactory(connectionFactory);
        if (this.messageConverter != null) {
            template.setMessageConverter(this.messageConverter);
        }
        template.setMandatory(determineMandatoryFlag());
        RabbitProperties.Template templateProperties = this.rabbitProperties.getTemplate();
        if (templateProperties.getRetry().isEnabled()) {
            template.setRetryTemplate(new RetryTemplateFactory(this.retryTemplateCustomizers).createRetryTemplate(templateProperties.getRetry(), RabbitRetryTemplateCustomizer.Target.SENDER));
        }
        templateProperties.getClass();
        PropertyMapper.Source sourceAs = map.from(templateProperties::getReceiveTimeout).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        template.getClass();
        sourceAs.to((v1) -> {
            r1.setReceiveTimeout(v1);
        });
        templateProperties.getClass();
        PropertyMapper.Source sourceAs2 = map.from(templateProperties::getReplyTimeout).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        template.getClass();
        sourceAs2.to((v1) -> {
            r1.setReplyTimeout(v1);
        });
        templateProperties.getClass();
        PropertyMapper.Source sourceFrom = map.from(templateProperties::getExchange);
        template.getClass();
        sourceFrom.to(template::setExchange);
        templateProperties.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(templateProperties::getRoutingKey);
        template.getClass();
        sourceFrom2.to(template::setRoutingKey);
        templateProperties.getClass();
        PropertyMapper.Source sourceWhenNonNull = map.from(templateProperties::getDefaultReceiveQueue).whenNonNull();
        template.getClass();
        sourceWhenNonNull.to(template::setDefaultReceiveQueue);
    }

    private boolean determineMandatoryFlag() {
        Boolean mandatory = this.rabbitProperties.getTemplate().getMandatory();
        return mandatory != null ? mandatory.booleanValue() : this.rabbitProperties.isPublisherReturns();
    }
}
