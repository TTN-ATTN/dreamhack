package org.springframework.boot.web.server;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/WebServer.class */
public interface WebServer {
    void start() throws WebServerException;

    void stop() throws WebServerException;

    int getPort();

    default void shutDownGracefully(GracefulShutdownCallback callback) {
        callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
    }
}
