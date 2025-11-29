package org.springframework.boot.autoconfigure.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsMessageOperations;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;

@EnableConfigurationProperties({JmsProperties.class})
@AutoConfiguration
@ConditionalOnClass({Message.class, JmsTemplate.class})
@ConditionalOnBean({ConnectionFactory.class})
@Import({JmsAnnotationDrivenConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration.class */
public class JmsAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration$JmsTemplateConfiguration.class */
    protected static class JmsTemplateConfiguration {
        private final JmsProperties properties;
        private final ObjectProvider<DestinationResolver> destinationResolver;
        private final ObjectProvider<MessageConverter> messageConverter;

        public JmsTemplateConfiguration(JmsProperties properties, ObjectProvider<DestinationResolver> destinationResolver, ObjectProvider<MessageConverter> messageConverter) {
            this.properties = properties;
            this.destinationResolver = destinationResolver;
            this.messageConverter = messageConverter;
        }

        @ConditionalOnMissingBean({JmsOperations.class})
        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
            PropertyMapper map = PropertyMapper.get();
            JmsTemplate template = new JmsTemplate(connectionFactory);
            template.setPubSubDomain(this.properties.isPubSubDomain());
            ObjectProvider<DestinationResolver> objectProvider = this.destinationResolver;
            objectProvider.getClass();
            PropertyMapper.Source sourceWhenNonNull = map.from(objectProvider::getIfUnique).whenNonNull();
            template.getClass();
            sourceWhenNonNull.to(template::setDestinationResolver);
            ObjectProvider<MessageConverter> objectProvider2 = this.messageConverter;
            objectProvider2.getClass();
            PropertyMapper.Source sourceWhenNonNull2 = map.from(objectProvider2::getIfUnique).whenNonNull();
            template.getClass();
            sourceWhenNonNull2.to(template::setMessageConverter);
            mapTemplateProperties(this.properties.getTemplate(), template);
            return template;
        }

        private void mapTemplateProperties(JmsProperties.Template properties, JmsTemplate template) {
            PropertyMapper map = PropertyMapper.get();
            properties.getClass();
            PropertyMapper.Source sourceWhenNonNull = map.from(properties::getDefaultDestination).whenNonNull();
            template.getClass();
            sourceWhenNonNull.to(template::setDefaultDestinationName);
            properties.getClass();
            PropertyMapper.Source sourceAs = map.from(properties::getDeliveryDelay).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            sourceAs.to((v1) -> {
                r1.setDeliveryDelay(v1);
            });
            properties.getClass();
            PropertyMapper.Source sourceFrom = map.from(properties::determineQosEnabled);
            template.getClass();
            sourceFrom.to((v1) -> {
                r1.setExplicitQosEnabled(v1);
            });
            properties.getClass();
            PropertyMapper.Source sourceAs2 = map.from(properties::getDeliveryMode).whenNonNull().as((v0) -> {
                return v0.getValue();
            });
            template.getClass();
            sourceAs2.to((v1) -> {
                r1.setDeliveryMode(v1);
            });
            properties.getClass();
            PropertyMapper.Source sourceWhenNonNull2 = map.from(properties::getPriority).whenNonNull();
            template.getClass();
            sourceWhenNonNull2.to((v1) -> {
                r1.setPriority(v1);
            });
            properties.getClass();
            PropertyMapper.Source sourceAs3 = map.from(properties::getTimeToLive).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            sourceAs3.to((v1) -> {
                r1.setTimeToLive(v1);
            });
            properties.getClass();
            PropertyMapper.Source sourceAs4 = map.from(properties::getReceiveTimeout).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            sourceAs4.to((v1) -> {
                r1.setReceiveTimeout(v1);
            });
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JmsMessagingTemplate.class})
    @Import({JmsTemplateConfiguration.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration$MessagingTemplateConfiguration.class */
    protected static class MessagingTemplateConfiguration {
        protected MessagingTemplateConfiguration() {
        }

        @ConditionalOnMissingBean({JmsMessageOperations.class})
        @Bean
        @ConditionalOnSingleCandidate(JmsTemplate.class)
        public JmsMessagingTemplate jmsMessagingTemplate(JmsProperties properties, JmsTemplate jmsTemplate) {
            JmsMessagingTemplate messagingTemplate = new JmsMessagingTemplate(jmsTemplate);
            mapTemplateProperties(properties.getTemplate(), messagingTemplate);
            return messagingTemplate;
        }

        private void mapTemplateProperties(JmsProperties.Template properties, JmsMessagingTemplate messagingTemplate) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            properties.getClass();
            PropertyMapper.Source sourceFrom = map.from(properties::getDefaultDestination);
            messagingTemplate.getClass();
            sourceFrom.to(messagingTemplate::setDefaultDestinationName);
        }
    }
}
