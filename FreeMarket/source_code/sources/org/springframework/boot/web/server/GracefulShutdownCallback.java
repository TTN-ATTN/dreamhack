package org.springframework.boot.web.server;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/GracefulShutdownCallback.class */
public interface GracefulShutdownCallback {
    void shutdownComplete(GracefulShutdownResult result);
}
