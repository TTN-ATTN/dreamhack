package org.springframework.web.server;

import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/WebFilterChain.class */
public interface WebFilterChain {
    Mono<Void> filter(ServerWebExchange exchange);
}
