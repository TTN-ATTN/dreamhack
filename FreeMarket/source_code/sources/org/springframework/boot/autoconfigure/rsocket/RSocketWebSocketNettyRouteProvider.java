package org.springframework.boot.autoconfigure.rsocket;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.WebsocketRouteTransport;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.web.embedded.netty.NettyRouteProvider;
import reactor.netty.http.server.HttpServerRoutes;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketWebSocketNettyRouteProvider.class */
class RSocketWebSocketNettyRouteProvider implements NettyRouteProvider {
    private final String mappingPath;
    private final SocketAcceptor socketAcceptor;
    private final List<RSocketServerCustomizer> customizers;

    RSocketWebSocketNettyRouteProvider(String mappingPath, SocketAcceptor socketAcceptor, Stream<RSocketServerCustomizer> customizers) {
        this.mappingPath = mappingPath;
        this.socketAcceptor = socketAcceptor;
        this.customizers = (List) customizers.collect(Collectors.toList());
    }

    @Override // java.util.function.Function
    public HttpServerRoutes apply(HttpServerRoutes httpServerRoutes) {
        RSocketServer server = RSocketServer.create(this.socketAcceptor);
        this.customizers.forEach(customizer -> {
            customizer.customize(server);
        });
        ServerTransport.ConnectionAcceptor connectionAcceptor = server.asConnectionAcceptor();
        return httpServerRoutes.ws(this.mappingPath, WebsocketRouteTransport.newHandler(connectionAcceptor));
    }
}
