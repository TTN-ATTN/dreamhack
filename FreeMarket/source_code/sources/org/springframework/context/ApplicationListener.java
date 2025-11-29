package org.springframework.context;

import java.util.EventListener;
import java.util.function.Consumer;
import org.springframework.context.ApplicationEvent;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/ApplicationListener.class */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);

    static <T> ApplicationListener<PayloadApplicationEvent<T>> forPayload(Consumer<T> consumer) {
        return event -> {
            consumer.accept(event.getPayload());
        };
    }
}
