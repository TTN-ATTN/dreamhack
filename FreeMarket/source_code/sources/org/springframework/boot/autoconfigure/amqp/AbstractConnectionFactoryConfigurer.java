package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/AbstractConnectionFactoryConfigurer.class */
public abstract class AbstractConnectionFactoryConfigurer<T extends AbstractConnectionFactory> {
    private final RabbitProperties rabbitProperties;
    private ConnectionNameStrategy connectionNameStrategy;

    protected abstract void configure(T connectionFactory, RabbitProperties rabbitProperties);

    protected AbstractConnectionFactoryConfigurer(RabbitProperties properties) {
        Assert.notNull(properties, "RabbitProperties must not be null");
        this.rabbitProperties = properties;
    }

    protected final ConnectionNameStrategy getConnectionNameStrategy() {
        return this.connectionNameStrategy;
    }

    public final void setConnectionNameStrategy(ConnectionNameStrategy connectionNameStrategy) {
        this.connectionNameStrategy = connectionNameStrategy;
    }

    public final void configure(T connectionFactory) {
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties rabbitProperties = this.rabbitProperties;
        rabbitProperties.getClass();
        PropertyMapper.Source sourceFrom = map.from(rabbitProperties::determineAddresses);
        connectionFactory.getClass();
        sourceFrom.to(connectionFactory::setAddresses);
        RabbitProperties rabbitProperties2 = this.rabbitProperties;
        rabbitProperties2.getClass();
        PropertyMapper.Source sourceWhenNonNull = map.from(rabbitProperties2::getAddressShuffleMode).whenNonNull();
        connectionFactory.getClass();
        sourceWhenNonNull.to(connectionFactory::setAddressShuffleMode);
        PropertyMapper.Source sourceWhenNonNull2 = map.from((PropertyMapper) this.connectionNameStrategy).whenNonNull();
        connectionFactory.getClass();
        sourceWhenNonNull2.to(connectionFactory::setConnectionNameStrategy);
        configure(connectionFactory, this.rabbitProperties);
    }
}
