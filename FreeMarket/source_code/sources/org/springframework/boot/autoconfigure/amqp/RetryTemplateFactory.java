package org.springframework.boot.autoconfigure.amqp;

import java.util.List;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/RetryTemplateFactory.class */
class RetryTemplateFactory {
    private final List<RabbitRetryTemplateCustomizer> customizers;

    RetryTemplateFactory(List<RabbitRetryTemplateCustomizer> customizers) {
        this.customizers = customizers;
    }

    RetryTemplate createRetryTemplate(RabbitProperties.Retry properties, RabbitRetryTemplateCustomizer.Target target) {
        PropertyMapper map = PropertyMapper.get();
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        properties.getClass();
        PropertyMapper.Source sourceFrom = map.from(properties::getMaxAttempts);
        policy.getClass();
        sourceFrom.to((v1) -> {
            r1.setMaxAttempts(v1);
        });
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        properties.getClass();
        PropertyMapper.Source sourceAs = map.from(properties::getInitialInterval).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        backOffPolicy.getClass();
        sourceAs.to((v1) -> {
            r1.setInitialInterval(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(properties::getMultiplier);
        backOffPolicy.getClass();
        sourceFrom2.to((v1) -> {
            r1.setMultiplier(v1);
        });
        properties.getClass();
        PropertyMapper.Source sourceAs2 = map.from(properties::getMaxInterval).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        backOffPolicy.getClass();
        sourceAs2.to((v1) -> {
            r1.setMaxInterval(v1);
        });
        template.setBackOffPolicy(backOffPolicy);
        if (this.customizers != null) {
            for (RabbitRetryTemplateCustomizer customizer : this.customizers) {
                customizer.customize(target, template);
            }
        }
        return template;
    }
}
