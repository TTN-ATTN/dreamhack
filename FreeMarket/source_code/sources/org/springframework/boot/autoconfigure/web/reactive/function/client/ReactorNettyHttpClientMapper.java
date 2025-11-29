package org.springframework.boot.autoconfigure.web.reactive.function.client;

import reactor.netty.http.client.HttpClient;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ReactorNettyHttpClientMapper.class */
public interface ReactorNettyHttpClientMapper {
    HttpClient configure(HttpClient httpClient);
}
