package org.springframework.boot.web.server;

import java.net.InetAddress;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/ConfigurableWebServerFactory.class */
public interface ConfigurableWebServerFactory extends WebServerFactory, ErrorPageRegistry {
    void setPort(int port);

    void setAddress(InetAddress address);

    void setErrorPages(Set<? extends ErrorPage> errorPages);

    void setSsl(Ssl ssl);

    void setSslStoreProvider(SslStoreProvider sslStoreProvider);

    void setHttp2(Http2 http2);

    void setCompression(Compression compression);

    void setServerHeader(String serverHeader);

    default void setShutdown(Shutdown shutdown) {
    }
}
