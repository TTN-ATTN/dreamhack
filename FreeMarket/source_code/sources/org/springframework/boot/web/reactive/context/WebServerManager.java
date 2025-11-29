package org.springframework.boot.web.reactive.context;

import java.util.function.Supplier;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.context.ApplicationEvent;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/WebServerManager.class */
class WebServerManager {
    private final ReactiveWebServerApplicationContext applicationContext;
    private final DelayedInitializationHttpHandler handler;
    private final WebServer webServer;

    WebServerManager(ReactiveWebServerApplicationContext applicationContext, ReactiveWebServerFactory factory, Supplier<HttpHandler> handlerSupplier, boolean lazyInit) {
        this.applicationContext = applicationContext;
        Assert.notNull(factory, "Factory must not be null");
        this.handler = new DelayedInitializationHttpHandler(handlerSupplier, lazyInit);
        this.webServer = factory.getWebServer(this.handler);
    }

    void start() throws WebServerException {
        this.handler.initializeHandler();
        this.webServer.start();
        this.applicationContext.publishEvent((ApplicationEvent) new ReactiveWebServerInitializedEvent(this.webServer, this.applicationContext));
    }

    void shutDownGracefully(GracefulShutdownCallback callback) {
        this.webServer.shutDownGracefully(callback);
    }

    void stop() throws WebServerException {
        this.webServer.stop();
    }

    WebServer getWebServer() {
        return this.webServer;
    }

    HttpHandler getHandler() {
        return this.handler;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/WebServerManager$DelayedInitializationHttpHandler.class */
    static final class DelayedInitializationHttpHandler implements HttpHandler {
        private final Supplier<HttpHandler> handlerSupplier;
        private final boolean lazyInit;
        private volatile HttpHandler delegate;

        private DelayedInitializationHttpHandler(Supplier<HttpHandler> handlerSupplier, boolean lazyInit) {
            this.delegate = this::handleUninitialized;
            this.handlerSupplier = handlerSupplier;
            this.lazyInit = lazyInit;
        }

        private Mono<Void> handleUninitialized(ServerHttpRequest request, ServerHttpResponse response) {
            throw new IllegalStateException("The HttpHandler has not yet been initialized");
        }

        @Override // org.springframework.http.server.reactive.HttpHandler
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            return this.delegate.handle(request, response);
        }

        void initializeHandler() {
            this.delegate = this.lazyInit ? new LazyHttpHandler(this.handlerSupplier) : this.handlerSupplier.get();
        }

        HttpHandler getHandler() {
            return this.delegate;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/WebServerManager$LazyHttpHandler.class */
    private static final class LazyHttpHandler implements HttpHandler {
        private final Mono<HttpHandler> delegate;

        private LazyHttpHandler(Supplier<HttpHandler> handlerSupplier) {
            this.delegate = Mono.fromSupplier(handlerSupplier);
        }

        @Override // org.springframework.http.server.reactive.HttpHandler
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            return this.delegate.flatMap(handler -> {
                return handler.handle(request, response);
            });
        }
    }
}
