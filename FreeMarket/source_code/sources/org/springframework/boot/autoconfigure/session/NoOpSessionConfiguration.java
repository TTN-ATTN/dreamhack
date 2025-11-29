package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.SessionRepository;

@ConditionalOnMissingBean({SessionRepository.class})
@Configuration(proxyBeanMethods = false)
@Conditional({ServletSessionCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/NoOpSessionConfiguration.class */
class NoOpSessionConfiguration {
    NoOpSessionConfiguration() {
    }
}
