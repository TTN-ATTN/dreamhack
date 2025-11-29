package org.springframework.boot.web.context;

import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationEvent;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/context/WebServerInitializedEvent.class */
public abstract class WebServerInitializedEvent extends ApplicationEvent {
    public abstract WebServerApplicationContext getApplicationContext();

    protected WebServerInitializedEvent(WebServer webServer) {
        super(webServer);
    }

    public WebServer getWebServer() {
        return getSource();
    }

    @Override // java.util.EventObject
    public WebServer getSource() {
        return (WebServer) super.getSource();
    }
}
