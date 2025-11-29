package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@EnableConfigurationProperties({RedisSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RedisTemplate.class, RedisIndexedSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({RedisConnectionFactory.class})
@Conditional({ServletSessionCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/RedisSessionConfiguration.class */
class RedisSessionConfiguration {
    RedisSessionConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    ConfigureRedisAction configureRedisAction(RedisSessionProperties redisSessionProperties) {
        switch (redisSessionProperties.getConfigureAction()) {
            case NOTIFY_KEYSPACE_EVENTS:
                return new ConfigureNotifyKeyspaceEventsAction();
            case NONE:
                return ConfigureRedisAction.NO_OP;
            default:
                throw new IllegalStateException("Unsupported redis configure action '" + redisSessionProperties.getConfigureAction() + "'.");
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/RedisSessionConfiguration$SpringBootRedisHttpSessionConfiguration.class */
    public static class SpringBootRedisHttpSessionConfiguration extends RedisHttpSessionConfiguration {
        @Autowired
        public void customize(SessionProperties sessionProperties, RedisSessionProperties redisSessionProperties, ServerProperties serverProperties) {
            Duration timeout = sessionProperties.determineTimeout(() -> {
                return serverProperties.getServlet().getSession().getTimeout();
            });
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds((int) timeout.getSeconds());
            }
            setRedisNamespace(redisSessionProperties.getNamespace());
            setFlushMode(redisSessionProperties.getFlushMode());
            setSaveMode(redisSessionProperties.getSaveMode());
            setCleanupCron(redisSessionProperties.getCleanupCron());
        }
    }
}
