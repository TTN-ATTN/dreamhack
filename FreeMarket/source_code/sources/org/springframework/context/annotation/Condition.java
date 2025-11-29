package org.springframework.context.annotation;

import org.springframework.core.type.AnnotatedTypeMetadata;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/annotation/Condition.class */
public interface Condition {
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
