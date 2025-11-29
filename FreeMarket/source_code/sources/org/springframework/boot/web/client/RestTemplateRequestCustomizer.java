package org.springframework.boot.web.client;

import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/client/RestTemplateRequestCustomizer.class */
public interface RestTemplateRequestCustomizer<T extends ClientHttpRequest> {
    void customize(T request);
}
