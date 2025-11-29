package org.springframework.web.cors.reactive;

import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/cors/reactive/CorsProcessor.class */
public interface CorsProcessor {
    boolean process(@Nullable CorsConfiguration configuration, ServerWebExchange exchange);
}
