package org.springframework.boot.web.context;

import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/context/WebServerApplicationContext.class */
public interface WebServerApplicationContext extends ApplicationContext {
    WebServer getWebServer();

    String getServerNamespace();

    static boolean hasServerNamespace(ApplicationContext context, String serverNamespace) {
        return (context instanceof WebServerApplicationContext) && ObjectUtils.nullSafeEquals(((WebServerApplicationContext) context).getServerNamespace(), serverNamespace);
    }

    static String getServerNamespace(ApplicationContext context) {
        if (context instanceof WebServerApplicationContext) {
            return ((WebServerApplicationContext) context).getServerNamespace();
        }
        return null;
    }
}
