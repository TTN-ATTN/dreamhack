package org.springframework.web.cors.reactive;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/cors/reactive/PreFlightRequestHandler.class */
public interface PreFlightRequestHandler {
    Mono<Void> handlePreFlight(ServerWebExchange exchange);
}
