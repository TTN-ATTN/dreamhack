package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/annotation/ScopeMetadataResolver.class */
public interface ScopeMetadataResolver {
    ScopeMetadata resolveScopeMetadata(BeanDefinition definition);
}
