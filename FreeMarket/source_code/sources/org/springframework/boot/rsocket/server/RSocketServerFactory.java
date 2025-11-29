package org.springframework.boot.rsocket.server;

import io.rsocket.SocketAcceptor;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/server/RSocketServerFactory.class */
public interface RSocketServerFactory {
    RSocketServer create(SocketAcceptor socketAcceptor);
}
