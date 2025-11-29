package org.springframework.context;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/ApplicationEventPublisher.class */
public interface ApplicationEventPublisher {
    void publishEvent(Object event);

    default void publishEvent(ApplicationEvent event) {
        publishEvent((Object) event);
    }
}
