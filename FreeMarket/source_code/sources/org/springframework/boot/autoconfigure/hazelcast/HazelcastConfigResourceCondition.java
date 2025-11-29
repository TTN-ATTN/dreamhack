package org.springframework.boot.autoconfigure.hazelcast;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ResourceCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastConfigResourceCondition.class */
public abstract class HazelcastConfigResourceCondition extends ResourceCondition {
    protected static final String HAZELCAST_CONFIG_PROPERTY = "spring.hazelcast.config";
    private final String configSystemProperty;

    protected HazelcastConfigResourceCondition(String configSystemProperty, String... resourceLocations) {
        super("Hazelcast", HAZELCAST_CONFIG_PROPERTY, resourceLocations);
        Assert.notNull(configSystemProperty, "ConfigSystemProperty must not be null");
        this.configSystemProperty = configSystemProperty;
    }

    @Override // org.springframework.boot.autoconfigure.condition.ResourceCondition
    protected ConditionOutcome getResourceOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (System.getProperty(this.configSystemProperty) != null) {
            return ConditionOutcome.match(startConditionMessage().because("System property '" + this.configSystemProperty + "' is set."));
        }
        return super.getResourceOutcome(context, metadata);
    }
}
