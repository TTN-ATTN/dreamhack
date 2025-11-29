package org.springframework.boot.web.reactive.server;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.http.server.reactive.HttpHandler;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/server/ReactiveWebServerFactory.class */
public interface ReactiveWebServerFactory extends WebServerFactory {
    WebServer getWebServer(HttpHandler httpHandler);
}
