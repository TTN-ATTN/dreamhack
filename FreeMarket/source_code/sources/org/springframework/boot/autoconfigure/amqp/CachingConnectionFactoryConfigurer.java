package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/CachingConnectionFactoryConfigurer.class */
public class CachingConnectionFactoryConfigurer extends AbstractConnectionFactoryConfigurer<CachingConnectionFactory> {
    public CachingConnectionFactoryConfigurer(RabbitProperties properties) {
        super(properties);
    }

    @Override // org.springframework.boot.autoconfigure.amqp.AbstractConnectionFactoryConfigurer
    public void configure(CachingConnectionFactory connectionFactory, RabbitProperties rabbitProperties) {
        PropertyMapper map = PropertyMapper.get();
        rabbitProperties.getClass();
        PropertyMapper.Source sourceFrom = map.from(rabbitProperties::isPublisherReturns);
        connectionFactory.getClass();
        sourceFrom.to((v1) -> {
            r1.setPublisherReturns(v1);
        });
        rabbitProperties.getClass();
        PropertyMapper.Source sourceWhenNonNull = map.from(rabbitProperties::getPublisherConfirmType).whenNonNull();
        connectionFactory.getClass();
        sourceWhenNonNull.to(connectionFactory::setPublisherConfirmType);
        RabbitProperties.Cache.Channel channel = rabbitProperties.getCache().getChannel();
        channel.getClass();
        PropertyMapper.Source sourceWhenNonNull2 = map.from(channel::getSize).whenNonNull();
        connectionFactory.getClass();
        sourceWhenNonNull2.to((v1) -> {
            r1.setChannelCacheSize(v1);
        });
        channel.getClass();
        PropertyMapper.Source sourceAs = map.from(channel::getCheckoutTimeout).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        connectionFactory.getClass();
        sourceAs.to((v1) -> {
            r1.setChannelCheckoutTimeout(v1);
        });
        RabbitProperties.Cache.Connection connection = rabbitProperties.getCache().getConnection();
        connection.getClass();
        PropertyMapper.Source sourceWhenNonNull3 = map.from(connection::getMode).whenNonNull();
        connectionFactory.getClass();
        sourceWhenNonNull3.to(connectionFactory::setCacheMode);
        connection.getClass();
        PropertyMapper.Source sourceWhenNonNull4 = map.from(connection::getSize).whenNonNull();
        connectionFactory.getClass();
        sourceWhenNonNull4.to((v1) -> {
            r1.setConnectionCacheSize(v1);
        });
    }
}
