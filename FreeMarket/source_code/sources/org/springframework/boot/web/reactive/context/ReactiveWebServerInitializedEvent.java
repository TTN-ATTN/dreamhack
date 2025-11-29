package org.springframework.boot.web.reactive.context;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/ReactiveWebServerInitializedEvent.class */
public class ReactiveWebServerInitializedEvent extends WebServerInitializedEvent {
    private final ReactiveWebServerApplicationContext applicationContext;

    public ReactiveWebServerInitializedEvent(WebServer webServer, ReactiveWebServerApplicationContext applicationContext) {
        super(webServer);
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.boot.web.context.WebServerInitializedEvent
    public ReactiveWebServerApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
