package org.springframework.boot.web.servlet.server;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/server/ServletWebServerFactory.class */
public interface ServletWebServerFactory extends WebServerFactory {
    WebServer getWebServer(ServletContextInitializer... initializers);
}
