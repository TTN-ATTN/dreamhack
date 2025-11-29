package org.springframework.boot.autoconfigure.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/kafka/ConcurrentKafkaListenerContainerFactoryConfigurer.class */
public class ConcurrentKafkaListenerContainerFactoryConfigurer {
    private KafkaProperties properties;
    private MessageConverter messageConverter;
    private RecordFilterStrategy<Object, Object> recordFilterStrategy;
    private KafkaTemplate<Object, Object> replyTemplate;
    private KafkaAwareTransactionManager<Object, Object> transactionManager;
    private ConsumerAwareRebalanceListener rebalanceListener;
    private ErrorHandler errorHandler;
    private BatchErrorHandler batchErrorHandler;
    private CommonErrorHandler commonErrorHandler;
    private AfterRollbackProcessor<Object, Object> afterRollbackProcessor;
    private RecordInterceptor<Object, Object> recordInterceptor;

    void setKafkaProperties(KafkaProperties properties) {
        this.properties = properties;
    }

    void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    void setRecordFilterStrategy(RecordFilterStrategy<Object, Object> recordFilterStrategy) {
        this.recordFilterStrategy = recordFilterStrategy;
    }

    void setReplyTemplate(KafkaTemplate<Object, Object> replyTemplate) {
        this.replyTemplate = replyTemplate;
    }

    void setTransactionManager(KafkaAwareTransactionManager<Object, Object> transactionManager) {
        this.transactionManager = transactionManager;
    }

    void setRebalanceListener(ConsumerAwareRebalanceListener rebalanceListener) {
        this.rebalanceListener = rebalanceListener;
    }

    void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    void setBatchErrorHandler(BatchErrorHandler batchErrorHandler) {
        this.batchErrorHandler = batchErrorHandler;
    }

    public void setCommonErrorHandler(CommonErrorHandler commonErrorHandler) {
        this.commonErrorHandler = commonErrorHandler;
    }

    void setAfterRollbackProcessor(AfterRollbackProcessor<Object, Object> afterRollbackProcessor) {
        this.afterRollbackProcessor = afterRollbackProcessor;
    }

    void setRecordInterceptor(RecordInterceptor<Object, Object> recordInterceptor) {
        this.recordInterceptor = recordInterceptor;
    }

    public void configure(ConcurrentKafkaListenerContainerFactory<Object, Object> listenerFactory, ConsumerFactory<Object, Object> consumerFactory) {
        listenerFactory.setConsumerFactory(consumerFactory);
        configureListenerFactory(listenerFactory);
        configureContainer(listenerFactory.getContainerProperties());
    }

    private void configureListenerFactory(ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        properties.getClass();
        PropertyMapper.Source sourceFrom = map.from(properties::getConcurrency);
        factory.getClass();
        sourceFrom.to(factory::setConcurrency);
        PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) this.messageConverter);
        factory.getClass();
        sourceFrom2.to(factory::setMessageConverter);
        PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) this.recordFilterStrategy);
        factory.getClass();
        sourceFrom3.to(factory::setRecordFilterStrategy);
        PropertyMapper.Source sourceFrom4 = map.from((PropertyMapper) this.replyTemplate);
        factory.getClass();
        sourceFrom4.to(factory::setReplyTemplate);
        if (properties.getType().equals(KafkaProperties.Listener.Type.BATCH)) {
            factory.setBatchListener(true);
            factory.setBatchErrorHandler(this.batchErrorHandler);
        } else {
            factory.setErrorHandler(this.errorHandler);
        }
        PropertyMapper.Source sourceFrom5 = map.from((PropertyMapper) this.commonErrorHandler);
        factory.getClass();
        sourceFrom5.to(factory::setCommonErrorHandler);
        PropertyMapper.Source sourceFrom6 = map.from((PropertyMapper) this.afterRollbackProcessor);
        factory.getClass();
        sourceFrom6.to(factory::setAfterRollbackProcessor);
        PropertyMapper.Source sourceFrom7 = map.from((PropertyMapper) this.recordInterceptor);
        factory.getClass();
        sourceFrom7.to(factory::setRecordInterceptor);
    }

    private void configureContainer(ContainerProperties container) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        properties.getClass();
        PropertyMapper.Source sourceFrom = map.from(properties::getAckMode);
        container.getClass();
        sourceFrom.to(container::setAckMode);
        properties.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(properties::getClientId);
        container.getClass();
        sourceFrom2.to(container::setClientId);
        properties.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(properties::getAckCount);
        container.getClass();
        sourceFrom3.to((v1) -> {
            r1.setAckCount(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceAs = map.from(properties::getAckTime).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        sourceAs.to((v1) -> {
            r1.setAckTime(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceAs2 = map.from(properties::getPollTimeout).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        sourceAs2.to((v1) -> {
            r1.setPollTimeout(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(properties::getNoPollThreshold);
        container.getClass();
        sourceFrom4.to((v1) -> {
            r1.setNoPollThreshold(v1);
        });
        PropertyMapper.Source sourceAs3 = map.from((PropertyMapper) properties.getIdleBetweenPolls()).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        sourceAs3.to((v1) -> {
            r1.setIdleBetweenPolls(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceAs4 = map.from(properties::getIdleEventInterval).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        sourceAs4.to(container::setIdleEventInterval);
        properties.getClass();
        PropertyMapper.Source sourceAs5 = map.from(properties::getIdlePartitionEventInterval).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        sourceAs5.to(container::setIdlePartitionEventInterval);
        properties.getClass();
        PropertyMapper.Source sourceAs6 = map.from(properties::getMonitorInterval).as((v0) -> {
            return v0.getSeconds();
        }).as((v0) -> {
            return v0.intValue();
        });
        container.getClass();
        sourceAs6.to((v1) -> {
            r1.setMonitorInterval(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceFrom5 = map.from(properties::getLogContainerConfig);
        container.getClass();
        sourceFrom5.to((v1) -> {
            r1.setLogContainerConfig(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceFrom6 = map.from(properties::isOnlyLogRecordMetadata);
        container.getClass();
        sourceFrom6.to((v1) -> {
            r1.setOnlyLogRecordMetadata(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceFrom7 = map.from(properties::isMissingTopicsFatal);
        container.getClass();
        sourceFrom7.to((v1) -> {
            r1.setMissingTopicsFatal(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceFrom8 = map.from(properties::isImmediateStop);
        container.getClass();
        sourceFrom8.to((v1) -> {
            r1.setStopImmediate(v1);
        });
        PropertyMapper.Source sourceFrom9 = map.from((PropertyMapper) this.transactionManager);
        container.getClass();
        sourceFrom9.to((v1) -> {
            r1.setTransactionManager(v1);
        });
        PropertyMapper.Source sourceFrom10 = map.from((PropertyMapper) this.rebalanceListener);
        container.getClass();
        sourceFrom10.to((v1) -> {
            r1.setConsumerRebalanceListener(v1);
        });
    }
}
