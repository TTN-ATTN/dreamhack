package org.springframework.boot.rsocket.server;

import java.net.InetAddress;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.util.unit.DataSize;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/server/ConfigurableRSocketServerFactory.class */
public interface ConfigurableRSocketServerFactory {
    void setPort(int port);

    void setFragmentSize(DataSize fragmentSize);

    void setAddress(InetAddress address);

    void setTransport(RSocketServer.Transport transport);

    void setSsl(Ssl ssl);

    void setSslStoreProvider(SslStoreProvider sslStoreProvider);
}
