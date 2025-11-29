package org.springframework.boot.rsocket.server;

import java.net.InetSocketAddress;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/server/RSocketServer.class */
public interface RSocketServer {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/server/RSocketServer$Transport.class */
    public enum Transport {
        TCP,
        WEBSOCKET
    }

    void start() throws RSocketServerException;

    void stop() throws RSocketServerException;

    InetSocketAddress address();
}
