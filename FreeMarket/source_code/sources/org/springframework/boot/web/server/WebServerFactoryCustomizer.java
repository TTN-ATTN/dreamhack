package org.springframework.boot.web.server;

import org.springframework.boot.web.server.WebServerFactory;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/WebServerFactoryCustomizer.class */
public interface WebServerFactoryCustomizer<T extends WebServerFactory> {
    void customize(T factory);
}
