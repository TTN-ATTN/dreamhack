package org.springframework.web.server.session;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/session/WebSessionManager.class */
public interface WebSessionManager {
    Mono<WebSession> getSession(ServerWebExchange exchange);
}
