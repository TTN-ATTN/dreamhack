package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@EnableConfigurationProperties({HazelcastProperties.class})
@AutoConfiguration
@ConditionalOnClass({HazelcastInstance.class})
@Conditional({HazelcastDataGridCondition.class})
@Import({HazelcastClientConfiguration.class, HazelcastServerConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastAutoConfiguration.class */
public class HazelcastAutoConfiguration {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastAutoConfiguration$HazelcastDataGridCondition.class */
    static class HazelcastDataGridCondition extends SpringBootCondition {
        private static final String HAZELCAST_JET_CONFIG_FILE = "classpath:/hazelcast-jet-default.yaml";

        HazelcastDataGridCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition(HazelcastDataGridCondition.class.getSimpleName(), new Object[0]);
            Resource resource = context.getResourceLoader().getResource(HAZELCAST_JET_CONFIG_FILE);
            if (resource.exists()) {
                return ConditionOutcome.noMatch(message.because("Found Hazelcast Jet on the classpath"));
            }
            return ConditionOutcome.match(message.because("Hazelcast Jet not found on the classpath"));
        }
    }
}
