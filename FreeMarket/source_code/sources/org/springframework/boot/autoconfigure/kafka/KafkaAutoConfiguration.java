package org.springframework.boot.autoconfigure.kafka;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.security.jaas.KafkaJaasLoginModuleInitializer;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.retry.backoff.BackOffPolicyBuilder;

@EnableConfigurationProperties({KafkaProperties.class})
@AutoConfiguration
@ConditionalOnClass({KafkaTemplate.class})
@Import({KafkaAnnotationDrivenConfiguration.class, KafkaStreamsAnnotationDrivenConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/kafka/KafkaAutoConfiguration.class */
public class KafkaAutoConfiguration {
    private final KafkaProperties properties;

    public KafkaAutoConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({KafkaTemplate.class})
    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory, ProducerListener<Object, Object> kafkaProducerListener, ObjectProvider<RecordMessageConverter> messageConverter) throws BeansException {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        kafkaTemplate.getClass();
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        PropertyMapper.Source sourceFrom = map.from((PropertyMapper) kafkaProducerListener);
        kafkaTemplate.getClass();
        sourceFrom.to(kafkaTemplate::setProducerListener);
        PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) this.properties.getTemplate().getDefaultTopic());
        kafkaTemplate.getClass();
        sourceFrom2.to(kafkaTemplate::setDefaultTopic);
        PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) this.properties.getTemplate().getTransactionIdPrefix());
        kafkaTemplate.getClass();
        sourceFrom3.to(kafkaTemplate::setTransactionIdPrefix);
        return kafkaTemplate;
    }

    @ConditionalOnMissingBean({ProducerListener.class})
    @Bean
    public LoggingProducerListener<Object, Object> kafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    @ConditionalOnMissingBean({ConsumerFactory.class})
    @Bean
    public DefaultKafkaConsumerFactory<?, ?> kafkaConsumerFactory(ObjectProvider<DefaultKafkaConsumerFactoryCustomizer> customizers) {
        DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties());
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(factory);
        });
        return factory;
    }

    @ConditionalOnMissingBean({ProducerFactory.class})
    @Bean
    public DefaultKafkaProducerFactory<?, ?> kafkaProducerFactory(ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers) {
        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(this.properties.buildProducerProperties());
        String transactionIdPrefix = this.properties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(factory);
        });
        return factory;
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.kafka.producer.transaction-id-prefix"})
    @Bean
    public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.kafka.jaas.enabled"})
    @Bean
    public KafkaJaasLoginModuleInitializer kafkaJaasInitializer() throws IOException {
        KafkaJaasLoginModuleInitializer jaas = new KafkaJaasLoginModuleInitializer();
        KafkaProperties.Jaas jaasProperties = this.properties.getJaas();
        if (jaasProperties.getControlFlag() != null) {
            jaas.setControlFlag(jaasProperties.getControlFlag());
        }
        if (jaasProperties.getLoginModule() != null) {
            jaas.setLoginModule(jaasProperties.getLoginModule());
        }
        jaas.setOptions(jaasProperties.getOptions());
        return jaas;
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaAdmin kafkaAdmin() {
        KafkaAdmin kafkaAdmin = new KafkaAdmin(this.properties.buildAdminProperties());
        kafkaAdmin.setFatalIfBrokerNotAvailable(this.properties.getAdmin().isFailFast());
        return kafkaAdmin;
    }

    @ConditionalOnProperty(name = {"spring.kafka.retry.topic.enabled"})
    @Bean
    @ConditionalOnSingleCandidate(KafkaTemplate.class)
    public RetryTopicConfiguration kafkaRetryTopicConfiguration(KafkaTemplate<?, ?> kafkaTemplate) {
        KafkaProperties.Retry.Topic retryTopic = this.properties.getRetry().getTopic();
        RetryTopicConfigurationBuilder builder = RetryTopicConfigurationBuilder.newInstance().maxAttempts(retryTopic.getAttempts()).useSingleTopicForFixedDelays().suffixTopicsWithIndexValues().doNotAutoCreateRetryTopics();
        setBackOffPolicy(builder, retryTopic);
        return builder.create(kafkaTemplate);
    }

    private static void setBackOffPolicy(RetryTopicConfigurationBuilder builder, KafkaProperties.Retry.Topic retryTopic) {
        long delay = retryTopic.getDelay() != null ? retryTopic.getDelay().toMillis() : 0L;
        if (delay > 0) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            BackOffPolicyBuilder backOffPolicy = BackOffPolicyBuilder.newBuilder();
            PropertyMapper.Source sourceFrom = map.from((PropertyMapper) Long.valueOf(delay));
            backOffPolicy.getClass();
            sourceFrom.to((v1) -> {
                r1.delay(v1);
            });
            PropertyMapper.Source sourceAs = map.from((PropertyMapper) retryTopic.getMaxDelay()).as((v0) -> {
                return v0.toMillis();
            });
            backOffPolicy.getClass();
            sourceAs.to((v1) -> {
                r1.maxDelay(v1);
            });
            PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) Double.valueOf(retryTopic.getMultiplier()));
            backOffPolicy.getClass();
            sourceFrom2.to((v1) -> {
                r1.multiplier(v1);
            });
            PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) Boolean.valueOf(retryTopic.isRandomBackOff()));
            backOffPolicy.getClass();
            sourceFrom3.to((v1) -> {
                r1.random(v1);
            });
            builder.customBackoff(backOffPolicy.build());
            return;
        }
        builder.noBackoff();
    }
}
