package org.springframework.boot.autoconfigure.amqp;

import org.springframework.retry.support.RetryTemplate;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/RabbitRetryTemplateCustomizer.class */
public interface RabbitRetryTemplateCustomizer {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/amqp/RabbitRetryTemplateCustomizer$Target.class */
    public enum Target {
        SENDER,
        LISTENER
    }

    void customize(Target target, RetryTemplate retryTemplate);
}
