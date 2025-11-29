package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.data.mongo.ReactiveMongoSessionRepository;
import org.springframework.session.data.mongo.config.annotation.web.reactive.ReactiveMongoWebSessionConfiguration;

@EnableConfigurationProperties({MongoSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ReactiveMongoOperations.class, ReactiveMongoSessionRepository.class})
@ConditionalOnMissingBean({ReactiveSessionRepository.class})
@ConditionalOnBean({ReactiveMongoOperations.class})
@Conditional({ReactiveSessionCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/MongoReactiveSessionConfiguration.class */
class MongoReactiveSessionConfiguration {
    MongoReactiveSessionConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/MongoReactiveSessionConfiguration$SpringBootReactiveMongoWebSessionConfiguration.class */
    static class SpringBootReactiveMongoWebSessionConfiguration extends ReactiveMongoWebSessionConfiguration {
        SpringBootReactiveMongoWebSessionConfiguration() {
        }

        @Autowired
        void customize(SessionProperties sessionProperties, MongoSessionProperties mongoSessionProperties, ServerProperties serverProperties) {
            Duration timeout = sessionProperties.determineTimeout(() -> {
                return serverProperties.getReactive().getSession().getTimeout();
            });
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds(Integer.valueOf((int) timeout.getSeconds()));
            }
            setCollectionName(mongoSessionProperties.getCollectionName());
        }
    }
}
