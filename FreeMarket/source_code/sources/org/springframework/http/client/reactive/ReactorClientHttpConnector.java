package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/reactive/ReactorClientHttpConnector.class */
public class ReactorClientHttpConnector implements ClientHttpConnector {
    private static final Function<HttpClient, HttpClient> defaultInitializer = client -> {
        return client.compress(true);
    };
    private final HttpClient httpClient;

    public ReactorClientHttpConnector() {
        this.httpClient = defaultInitializer.apply(HttpClient.create());
    }

    public ReactorClientHttpConnector(ReactorResourceFactory factory, Function<HttpClient, HttpClient> mapper) {
        ConnectionProvider provider = factory.getConnectionProvider();
        Assert.notNull(provider, "No ConnectionProvider: is ReactorResourceFactory not initialized yet?");
        this.httpClient = (HttpClient) defaultInitializer.andThen(mapper).andThen(applyLoopResources(factory)).apply(HttpClient.create(provider));
    }

    private static Function<HttpClient, HttpClient> applyLoopResources(ReactorResourceFactory factory) {
        return httpClient -> {
            LoopResources resources = factory.getLoopResources();
            Assert.notNull(resources, "No LoopResources: is ReactorResourceFactory not initialized yet?");
            return httpClient.runOn(resources);
        };
    }

    public ReactorClientHttpConnector(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        AtomicReference<ReactorClientHttpResponse> responseRef = new AtomicReference<>();
        return this.httpClient.request(io.netty.handler.codec.http.HttpMethod.valueOf(method.name())).uri(uri.toString()).send((request, outbound) -> {
            return (Mono) requestCallback.apply(adaptRequest(method, uri, request, outbound));
        }).responseConnection((response, connection) -> {
            responseRef.set(new ReactorClientHttpResponse(response, connection));
            return Mono.just((ClientHttpResponse) responseRef.get());
        }).next().doOnCancel(() -> {
            ReactorClientHttpResponse response2 = (ReactorClientHttpResponse) responseRef.get();
            if (response2 != null) {
                response2.releaseAfterCancel(method);
            }
        });
    }

    private ReactorClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound nettyOutbound) {
        return new ReactorClientHttpRequest(method, uri, request, nettyOutbound);
    }
}
