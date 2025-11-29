package org.springframework.web.server.session;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/session/WebSessionStore.class */
public interface WebSessionStore {
    Mono<WebSession> createWebSession();

    Mono<WebSession> retrieveSession(String sessionId);

    Mono<Void> removeSession(String sessionId);

    Mono<WebSession> updateLastAccessTime(WebSession webSession);
}
