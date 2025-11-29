package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/SimpleRabbitListenerContainerFactoryConfigurer.class */
public final class SimpleRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<SimpleRabbitListenerContainerFactory> {
    @Deprecated
    public SimpleRabbitListenerContainerFactoryConfigurer() {
    }

    public SimpleRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
        super(rabbitProperties);
    }

    @Override // org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer
    public void configure(SimpleRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.SimpleContainer config = getRabbitProperties().getListener().getSimple();
        configure(factory, connectionFactory, config);
        config.getClass();
        PropertyMapper.Source sourceWhenNonNull = map.from(config::getConcurrency).whenNonNull();
        factory.getClass();
        sourceWhenNonNull.to(factory::setConcurrentConsumers);
        config.getClass();
        PropertyMapper.Source sourceWhenNonNull2 = map.from(config::getMaxConcurrency).whenNonNull();
        factory.getClass();
        sourceWhenNonNull2.to(factory::setMaxConcurrentConsumers);
        config.getClass();
        PropertyMapper.Source sourceWhenNonNull3 = map.from(config::getBatchSize).whenNonNull();
        factory.getClass();
        sourceWhenNonNull3.to(factory::setBatchSize);
        config.getClass();
        PropertyMapper.Source sourceFrom = map.from(config::isConsumerBatchEnabled);
        factory.getClass();
        sourceFrom.to((v1) -> {
            r1.setConsumerBatchEnabled(v1);
        });
    }
}
