package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/event/GenericApplicationListener.class */
public interface GenericApplicationListener extends SmartApplicationListener {
    boolean supportsEventType(ResolvableType eventType);

    @Override // org.springframework.context.event.SmartApplicationListener
    default boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return supportsEventType(ResolvableType.forClass(eventType));
    }
}
