package org.springframework.http.client;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/ClientHttpRequestInitializer.class */
public interface ClientHttpRequestInitializer {
    void initialize(ClientHttpRequest request);
}
