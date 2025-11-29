package org.springframework.web.filter.reactive;

import java.util.Optional;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/filter/reactive/ServerWebExchangeContextFilter.class */
public class ServerWebExchangeContextFilter implements WebFilter {
    public static final String EXCHANGE_CONTEXT_ATTRIBUTE = ServerWebExchangeContextFilter.class.getName() + ".EXCHANGE_CONTEXT";

    @Override // org.springframework.web.server.WebFilter
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).contextWrite(cxt -> {
            return cxt.put(EXCHANGE_CONTEXT_ATTRIBUTE, exchange);
        });
    }

    public static Optional<ServerWebExchange> get(Context context) {
        return context.getOrEmpty(EXCHANGE_CONTEXT_ATTRIBUTE);
    }
}
