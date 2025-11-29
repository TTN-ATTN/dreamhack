package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.EnvironmentBuilder;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamOperations;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({StreamRabbitListenerContainerFactory.class})
@ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = {"type"}, havingValue = "stream")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/RabbitStreamConfiguration.class */
class RabbitStreamConfiguration {
    RabbitStreamConfiguration() {
    }

    @ConditionalOnMissingBean(name = {"rabbitListenerContainerFactory"})
    @Bean(name = {"rabbitListenerContainerFactory"})
    StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(Environment rabbitStreamEnvironment, RabbitProperties properties, ObjectProvider<ConsumerCustomizer> consumerCustomizer, ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer) throws BeansException {
        StreamRabbitListenerContainerFactory factory = new StreamRabbitListenerContainerFactory(rabbitStreamEnvironment);
        factory.setNativeListener(properties.getListener().getStream().isNativeListener());
        factory.getClass();
        consumerCustomizer.ifUnique(factory::setConsumerCustomizer);
        factory.getClass();
        containerCustomizer.ifUnique(factory::setContainerCustomizer);
        return factory;
    }

    @ConditionalOnMissingBean(name = {"rabbitStreamEnvironment"})
    @Bean(name = {"rabbitStreamEnvironment"})
    Environment rabbitStreamEnvironment(RabbitProperties properties) {
        return configure(Environment.builder(), properties).build();
    }

    @ConditionalOnMissingBean
    @Bean
    RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(RabbitProperties properties, ObjectProvider<MessageConverter> messageConverter, ObjectProvider<StreamMessageConverter> streamMessageConverter, ObjectProvider<ProducerCustomizer> producerCustomizer) {
        RabbitStreamTemplateConfigurer configurer = new RabbitStreamTemplateConfigurer();
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer.setStreamMessageConverter(streamMessageConverter.getIfUnique());
        configurer.setProducerCustomizer(producerCustomizer.getIfUnique());
        return configurer;
    }

    @ConditionalOnMissingBean({RabbitStreamOperations.class})
    @ConditionalOnProperty(prefix = "spring.rabbitmq.stream", name = {"name"})
    @Bean
    RabbitStreamTemplate rabbitStreamTemplate(Environment rabbitStreamEnvironment, RabbitProperties properties, RabbitStreamTemplateConfigurer configurer) {
        RabbitStreamTemplate template = new RabbitStreamTemplate(rabbitStreamEnvironment, properties.getStream().getName());
        configurer.configure(template);
        return template;
    }

    static EnvironmentBuilder configure(EnvironmentBuilder builder, RabbitProperties properties) {
        builder.lazyInitialization(true);
        RabbitProperties.Stream stream = properties.getStream();
        PropertyMapper mapper = PropertyMapper.get();
        PropertyMapper.Source sourceFrom = mapper.from((PropertyMapper) stream.getHost());
        builder.getClass();
        sourceFrom.to(builder::host);
        PropertyMapper.Source sourceFrom2 = mapper.from((PropertyMapper) Integer.valueOf(stream.getPort()));
        builder.getClass();
        sourceFrom2.to((v1) -> {
            r1.port(v1);
        });
        PropertyMapper.Source sourceFrom3 = mapper.from((PropertyMapper) stream.getUsername());
        properties.getClass();
        PropertyMapper.Source sourceWhenNonNull = sourceFrom3.as(withFallback(properties::getUsername)).whenNonNull();
        builder.getClass();
        sourceWhenNonNull.to(builder::username);
        PropertyMapper.Source sourceFrom4 = mapper.from((PropertyMapper) stream.getPassword());
        properties.getClass();
        PropertyMapper.Source sourceWhenNonNull2 = sourceFrom4.as(withFallback(properties::getPassword)).whenNonNull();
        builder.getClass();
        sourceWhenNonNull2.to(builder::password);
        return builder;
    }

    private static Function<String, String> withFallback(Supplier<String> fallback) {
        return value -> {
            return value != null ? value : (String) fallback.get();
        };
    }
}
