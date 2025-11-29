package org.springframework.boot.rsocket.messaging;

import org.springframework.messaging.rsocket.RSocketStrategies;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/messaging/RSocketStrategiesCustomizer.class */
public interface RSocketStrategiesCustomizer {
    void customize(RSocketStrategies.Builder strategies);
}
