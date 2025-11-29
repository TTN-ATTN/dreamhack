package org.springframework.boot.web.embedded.netty;

import java.util.function.Function;
import reactor.netty.http.server.HttpServerRoutes;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/netty/NettyRouteProvider.class */
public interface NettyRouteProvider extends Function<HttpServerRoutes, HttpServerRoutes> {
}
