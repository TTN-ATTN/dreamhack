package org.springframework.boot.autoconfigure.rsocket;

import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketMessageHandlerCustomizer.class */
public interface RSocketMessageHandlerCustomizer {
    void customize(RSocketMessageHandler messageHandler);
}
