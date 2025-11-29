package org.springframework.boot.autoconfigure.session;

import com.hazelcast.core.HazelcastInstance;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.SessionRepository;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.config.annotation.web.http.HazelcastHttpSessionConfiguration;

@EnableConfigurationProperties({HazelcastSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({HazelcastIndexedSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({HazelcastInstance.class})
@Conditional({ServletSessionCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/HazelcastSessionConfiguration.class */
class HazelcastSessionConfiguration {
    HazelcastSessionConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/HazelcastSessionConfiguration$SpringBootHazelcastHttpSessionConfiguration.class */
    public static class SpringBootHazelcastHttpSessionConfiguration extends HazelcastHttpSessionConfiguration {
        @Autowired
        public void customize(SessionProperties sessionProperties, HazelcastSessionProperties hazelcastSessionProperties, ServerProperties serverProperties) {
            Duration timeout = sessionProperties.determineTimeout(() -> {
                return serverProperties.getServlet().getSession().getTimeout();
            });
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds((int) timeout.getSeconds());
            }
            setSessionMapName(hazelcastSessionProperties.getMapName());
            setFlushMode(hazelcastSessionProperties.getFlushMode());
            setSaveMode(hazelcastSessionProperties.getSaveMode());
        }
    }
}
