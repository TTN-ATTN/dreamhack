package org.springframework.boot.web.reactive.function.client;

import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/function/client/WebClientCustomizer.class */
public interface WebClientCustomizer {
    void customize(WebClient.Builder webClientBuilder);
}
