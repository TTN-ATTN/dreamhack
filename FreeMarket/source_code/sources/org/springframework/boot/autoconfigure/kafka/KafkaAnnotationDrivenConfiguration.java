package org.springframework.boot.autoconfigure.kafka;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.BatchMessageConverter;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({EnableKafka.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/kafka/KafkaAnnotationDrivenConfiguration.class */
class KafkaAnnotationDrivenConfiguration {
    private final KafkaProperties properties;
    private final RecordMessageConverter messageConverter;
    private final RecordFilterStrategy<Object, Object> recordFilterStrategy;
    private final BatchMessageConverter batchMessageConverter;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final KafkaAwareTransactionManager<Object, Object> transactionManager;
    private final ConsumerAwareRebalanceListener rebalanceListener;
    private final ErrorHandler errorHandler;
    private final BatchErrorHandler batchErrorHandler;
    private final CommonErrorHandler commonErrorHandler;
    private final AfterRollbackProcessor<Object, Object> afterRollbackProcessor;
    private final RecordInterceptor<Object, Object> recordInterceptor;

    KafkaAnnotationDrivenConfiguration(KafkaProperties properties, ObjectProvider<RecordMessageConverter> messageConverter, ObjectProvider<RecordFilterStrategy<Object, Object>> recordFilterStrategy, ObjectProvider<BatchMessageConverter> batchMessageConverter, ObjectProvider<KafkaTemplate<Object, Object>> kafkaTemplate, ObjectProvider<KafkaAwareTransactionManager<Object, Object>> kafkaTransactionManager, ObjectProvider<ConsumerAwareRebalanceListener> rebalanceListener, ObjectProvider<ErrorHandler> errorHandler, ObjectProvider<BatchErrorHandler> batchErrorHandler, ObjectProvider<CommonErrorHandler> commonErrorHandler, ObjectProvider<AfterRollbackProcessor<Object, Object>> afterRollbackProcessor, ObjectProvider<RecordInterceptor<Object, Object>> recordInterceptor) {
        this.properties = properties;
        this.messageConverter = messageConverter.getIfUnique();
        this.recordFilterStrategy = recordFilterStrategy.getIfUnique();
        this.batchMessageConverter = batchMessageConverter.getIfUnique(() -> {
            return new BatchMessagingMessageConverter(this.messageConverter);
        });
        this.kafkaTemplate = kafkaTemplate.getIfUnique();
        this.transactionManager = kafkaTransactionManager.getIfUnique();
        this.rebalanceListener = rebalanceListener.getIfUnique();
        this.errorHandler = errorHandler.getIfUnique();
        this.batchErrorHandler = batchErrorHandler.getIfUnique();
        this.commonErrorHandler = commonErrorHandler.getIfUnique();
        this.afterRollbackProcessor = afterRollbackProcessor.getIfUnique();
        this.recordInterceptor = recordInterceptor.getIfUnique();
    }

    @ConditionalOnMissingBean
    @Bean
    ConcurrentKafkaListenerContainerFactoryConfigurer kafkaListenerContainerFactoryConfigurer() {
        ConcurrentKafkaListenerContainerFactoryConfigurer configurer = new ConcurrentKafkaListenerContainerFactoryConfigurer();
        configurer.setKafkaProperties(this.properties);
        configurer.setMessageConverter(this.properties.getListener().getType().equals(KafkaProperties.Listener.Type.BATCH) ? this.batchMessageConverter : this.messageConverter);
        configurer.setRecordFilterStrategy(this.recordFilterStrategy);
        configurer.setReplyTemplate(this.kafkaTemplate);
        configurer.setTransactionManager(this.transactionManager);
        configurer.setRebalanceListener(this.rebalanceListener);
        configurer.setErrorHandler(this.errorHandler);
        configurer.setBatchErrorHandler(this.batchErrorHandler);
        configurer.setCommonErrorHandler(this.commonErrorHandler);
        configurer.setAfterRollbackProcessor(this.afterRollbackProcessor);
        configurer.setRecordInterceptor(this.recordInterceptor);
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"kafkaListenerContainerFactory"})
    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory.getIfAvailable(() -> {
            return new DefaultKafkaConsumerFactory(this.properties.buildConsumerProperties());
        }));
        return factory;
    }

    @EnableKafka
    @ConditionalOnMissingBean(name = {"org.springframework.kafka.config.internalKafkaListenerAnnotationProcessor"})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/kafka/KafkaAnnotationDrivenConfiguration$EnableKafkaConfiguration.class */
    static class EnableKafkaConfiguration {
        EnableKafkaConfiguration() {
        }
    }
}
