package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/RabbitConnectionFactoryBeanConfigurer.class */
public class RabbitConnectionFactoryBeanConfigurer {
    private final RabbitProperties rabbitProperties;
    private final ResourceLoader resourceLoader;
    private CredentialsProvider credentialsProvider;
    private CredentialsRefreshService credentialsRefreshService;

    public RabbitConnectionFactoryBeanConfigurer(ResourceLoader resourceLoader, RabbitProperties properties) {
        this.resourceLoader = resourceLoader;
        this.rabbitProperties = properties;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public void setCredentialsRefreshService(CredentialsRefreshService credentialsRefreshService) {
        this.credentialsRefreshService = credentialsRefreshService;
    }

    public void configure(RabbitConnectionFactoryBean factory) {
        Assert.notNull(factory, "RabbitConnectionFactoryBean must not be null");
        factory.setResourceLoader(this.resourceLoader);
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties rabbitProperties = this.rabbitProperties;
        rabbitProperties.getClass();
        PropertyMapper.Source sourceWhenNonNull = map.from(rabbitProperties::determineHost).whenNonNull();
        factory.getClass();
        sourceWhenNonNull.to(factory::setHost);
        RabbitProperties rabbitProperties2 = this.rabbitProperties;
        rabbitProperties2.getClass();
        PropertyMapper.Source sourceFrom = map.from(rabbitProperties2::determinePort);
        factory.getClass();
        sourceFrom.to((v1) -> {
            r1.setPort(v1);
        });
        RabbitProperties rabbitProperties3 = this.rabbitProperties;
        rabbitProperties3.getClass();
        PropertyMapper.Source sourceWhenNonNull2 = map.from(rabbitProperties3::determineUsername).whenNonNull();
        factory.getClass();
        sourceWhenNonNull2.to(factory::setUsername);
        RabbitProperties rabbitProperties4 = this.rabbitProperties;
        rabbitProperties4.getClass();
        PropertyMapper.Source sourceWhenNonNull3 = map.from(rabbitProperties4::determinePassword).whenNonNull();
        factory.getClass();
        sourceWhenNonNull3.to(factory::setPassword);
        RabbitProperties rabbitProperties5 = this.rabbitProperties;
        rabbitProperties5.getClass();
        PropertyMapper.Source sourceWhenNonNull4 = map.from(rabbitProperties5::determineVirtualHost).whenNonNull();
        factory.getClass();
        sourceWhenNonNull4.to(factory::setVirtualHost);
        RabbitProperties rabbitProperties6 = this.rabbitProperties;
        rabbitProperties6.getClass();
        PropertyMapper.Source<Integer> sourceAsInt = map.from(rabbitProperties6::getRequestedHeartbeat).whenNonNull().asInt((v0) -> {
            return v0.getSeconds();
        });
        factory.getClass();
        sourceAsInt.to((v1) -> {
            r1.setRequestedHeartbeat(v1);
        });
        RabbitProperties rabbitProperties7 = this.rabbitProperties;
        rabbitProperties7.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(rabbitProperties7::getRequestedChannelMax);
        factory.getClass();
        sourceFrom2.to((v1) -> {
            r1.setRequestedChannelMax(v1);
        });
        RabbitProperties.Ssl ssl = this.rabbitProperties.getSsl();
        if (ssl.determineEnabled()) {
            factory.setUseSSL(true);
            ssl.getClass();
            PropertyMapper.Source sourceWhenNonNull5 = map.from(ssl::getAlgorithm).whenNonNull();
            factory.getClass();
            sourceWhenNonNull5.to(factory::setSslAlgorithm);
            ssl.getClass();
            PropertyMapper.Source sourceFrom3 = map.from(ssl::getKeyStoreType);
            factory.getClass();
            sourceFrom3.to(factory::setKeyStoreType);
            ssl.getClass();
            PropertyMapper.Source sourceFrom4 = map.from(ssl::getKeyStore);
            factory.getClass();
            sourceFrom4.to(factory::setKeyStore);
            ssl.getClass();
            PropertyMapper.Source sourceFrom5 = map.from(ssl::getKeyStorePassword);
            factory.getClass();
            sourceFrom5.to(factory::setKeyStorePassphrase);
            ssl.getClass();
            PropertyMapper.Source sourceWhenNonNull6 = map.from(ssl::getKeyStoreAlgorithm).whenNonNull();
            factory.getClass();
            sourceWhenNonNull6.to(factory::setKeyStoreAlgorithm);
            ssl.getClass();
            PropertyMapper.Source sourceFrom6 = map.from(ssl::getTrustStoreType);
            factory.getClass();
            sourceFrom6.to(factory::setTrustStoreType);
            ssl.getClass();
            PropertyMapper.Source sourceFrom7 = map.from(ssl::getTrustStore);
            factory.getClass();
            sourceFrom7.to(factory::setTrustStore);
            ssl.getClass();
            PropertyMapper.Source sourceFrom8 = map.from(ssl::getTrustStorePassword);
            factory.getClass();
            sourceFrom8.to(factory::setTrustStorePassphrase);
            ssl.getClass();
            PropertyMapper.Source sourceWhenNonNull7 = map.from(ssl::getTrustStoreAlgorithm).whenNonNull();
            factory.getClass();
            sourceWhenNonNull7.to(factory::setTrustStoreAlgorithm);
            ssl.getClass();
            map.from(ssl::isValidateServerCertificate).to(validate -> {
                factory.setSkipServerCertificateValidation(!validate.booleanValue());
            });
            ssl.getClass();
            PropertyMapper.Source sourceFrom9 = map.from(ssl::getVerifyHostname);
            factory.getClass();
            sourceFrom9.to((v1) -> {
                r1.setEnableHostnameVerification(v1);
            });
        }
        RabbitProperties rabbitProperties8 = this.rabbitProperties;
        rabbitProperties8.getClass();
        PropertyMapper.Source<Integer> sourceAsInt2 = map.from(rabbitProperties8::getConnectionTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        factory.getClass();
        sourceAsInt2.to((v1) -> {
            r1.setConnectionTimeout(v1);
        });
        RabbitProperties rabbitProperties9 = this.rabbitProperties;
        rabbitProperties9.getClass();
        PropertyMapper.Source<Integer> sourceAsInt3 = map.from(rabbitProperties9::getChannelRpcTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        factory.getClass();
        sourceAsInt3.to((v1) -> {
            r1.setChannelRpcTimeout(v1);
        });
        PropertyMapper.Source sourceWhenNonNull8 = map.from((PropertyMapper) this.credentialsProvider).whenNonNull();
        factory.getClass();
        sourceWhenNonNull8.to(factory::setCredentialsProvider);
        PropertyMapper.Source sourceWhenNonNull9 = map.from((PropertyMapper) this.credentialsRefreshService).whenNonNull();
        factory.getClass();
        sourceWhenNonNull9.to(factory::setCredentialsRefreshService);
    }
}
