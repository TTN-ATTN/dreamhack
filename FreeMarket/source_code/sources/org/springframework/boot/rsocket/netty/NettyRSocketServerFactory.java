package org.springframework.boot.rsocket.netty;

import io.rsocket.SocketAcceptor;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.boot.web.embedded.netty.SslServerCustomizer;
import org.springframework.boot.web.server.CertificateFileSslStoreProvider;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.util.Assert;
import org.springframework.util.unit.DataSize;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.AbstractProtocolSslContextSpec;
import reactor.netty.tcp.TcpServer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/netty/NettyRSocketServerFactory.class */
public class NettyRSocketServerFactory implements RSocketServerFactory, ConfigurableRSocketServerFactory {
    private DataSize fragmentSize;
    private InetAddress address;
    private ReactorResourceFactory resourceFactory;
    private Duration lifecycleTimeout;
    private Ssl ssl;
    private SslStoreProvider sslStoreProvider;
    private int port = 9898;
    private RSocketServer.Transport transport = RSocketServer.Transport.TCP;
    private List<RSocketServerCustomizer> rSocketServerCustomizers = new ArrayList();

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setPort(int port) {
        this.port = port;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setFragmentSize(DataSize fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setTransport(RSocketServer.Transport transport) {
        this.transport = transport;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    @Override // org.springframework.boot.rsocket.server.ConfigurableRSocketServerFactory
    public void setSslStoreProvider(SslStoreProvider sslStoreProvider) {
        this.sslStoreProvider = sslStoreProvider;
    }

    public void setResourceFactory(ReactorResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public void setRSocketServerCustomizers(Collection<? extends RSocketServerCustomizer> rSocketServerCustomizers) {
        Assert.notNull(rSocketServerCustomizers, "RSocketServerCustomizers must not be null");
        this.rSocketServerCustomizers = new ArrayList(rSocketServerCustomizers);
    }

    public void addRSocketServerCustomizers(RSocketServerCustomizer... rSocketServerCustomizers) {
        Assert.notNull(rSocketServerCustomizers, "RSocketServerCustomizers must not be null");
        this.rSocketServerCustomizers.addAll(Arrays.asList(rSocketServerCustomizers));
    }

    public void setLifecycleTimeout(Duration lifecycleTimeout) {
        this.lifecycleTimeout = lifecycleTimeout;
    }

    @Override // org.springframework.boot.rsocket.server.RSocketServerFactory
    public NettyRSocketServer create(SocketAcceptor socketAcceptor) {
        ServerTransport<CloseableChannel> transport = createTransport();
        io.rsocket.core.RSocketServer server = io.rsocket.core.RSocketServer.create(socketAcceptor);
        configureServer(server);
        Mono<CloseableChannel> starter = server.bind(transport);
        return new NettyRSocketServer(starter, this.lifecycleTimeout);
    }

    private void configureServer(io.rsocket.core.RSocketServer server) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source<Integer> sourceAsInt = map.from((PropertyMapper) this.fragmentSize).asInt((v0) -> {
            return v0.toBytes();
        });
        server.getClass();
        sourceAsInt.to((v1) -> {
            r1.fragment(v1);
        });
        this.rSocketServerCustomizers.forEach(customizer -> {
            customizer.customize(server);
        });
    }

    private ServerTransport<CloseableChannel> createTransport() {
        if (this.transport == RSocketServer.Transport.WEBSOCKET) {
            return createWebSocketTransport();
        }
        return createTcpTransport();
    }

    private ServerTransport<CloseableChannel> createWebSocketTransport() {
        HttpServer httpServer = HttpServer.create();
        if (this.resourceFactory != null) {
            httpServer = (HttpServer) httpServer.runOn(this.resourceFactory.getLoopResources());
        }
        if (this.ssl != null && this.ssl.isEnabled()) {
            httpServer = customizeSslConfiguration(httpServer);
        }
        return WebsocketServerTransport.create(httpServer.bindAddress(this::getListenAddress));
    }

    private HttpServer customizeSslConfiguration(HttpServer httpServer) {
        SslServerCustomizer sslServerCustomizer = new SslServerCustomizer(this.ssl, null, getOrCreateSslStoreProvider());
        return sslServerCustomizer.apply(httpServer);
    }

    private ServerTransport<CloseableChannel> createTcpTransport() {
        TcpServer tcpServer = TcpServer.create();
        if (this.resourceFactory != null) {
            tcpServer = tcpServer.runOn(this.resourceFactory.getLoopResources());
        }
        if (this.ssl != null && this.ssl.isEnabled()) {
            TcpSslServerCustomizer sslServerCustomizer = new TcpSslServerCustomizer(this.ssl, getOrCreateSslStoreProvider());
            tcpServer = sslServerCustomizer.apply(tcpServer);
        }
        return TcpServerTransport.create(tcpServer.bindAddress(this::getListenAddress));
    }

    private SslStoreProvider getOrCreateSslStoreProvider() {
        if (this.sslStoreProvider != null) {
            return this.sslStoreProvider;
        }
        return CertificateFileSslStoreProvider.from(this.ssl);
    }

    private InetSocketAddress getListenAddress() {
        if (this.address != null) {
            return new InetSocketAddress(this.address.getHostAddress(), this.port);
        }
        return new InetSocketAddress(this.port);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/netty/NettyRSocketServerFactory$TcpSslServerCustomizer.class */
    private static final class TcpSslServerCustomizer extends SslServerCustomizer {
        private TcpSslServerCustomizer(Ssl ssl, SslStoreProvider sslStoreProvider) {
            super(ssl, null, sslStoreProvider);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public TcpServer apply(TcpServer server) {
            AbstractProtocolSslContextSpec<?> sslContextSpec = createSslContextSpec();
            return server.secure(spec -> {
                spec.sslContext(sslContextSpec);
            });
        }
    }
}
