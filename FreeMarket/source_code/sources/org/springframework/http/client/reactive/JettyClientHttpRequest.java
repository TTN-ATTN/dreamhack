package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.net.URI;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.reactive.client.ContentChunk;
import org.eclipse.jetty.reactive.client.ReactiveRequest;
import org.eclipse.jetty.util.Callback;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/reactive/JettyClientHttpRequest.class */
class JettyClientHttpRequest extends AbstractClientHttpRequest {
    private final Request jettyRequest;
    private final DataBufferFactory bufferFactory;
    private final ReactiveRequest.Builder builder;

    public JettyClientHttpRequest(Request jettyRequest, DataBufferFactory bufferFactory) {
        this.jettyRequest = jettyRequest;
        this.bufferFactory = bufferFactory;
        this.builder = ReactiveRequest.newBuilder(this.jettyRequest).abortOnCancel(true);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.jettyRequest.getMethod());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public URI getURI() {
        return this.jettyRequest.getURI();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return doCommit();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.jettyRequest;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return Mono.create(sink -> {
            ReactiveRequest.Content content = (ReactiveRequest.Content) Flux.from(body).map(buffer -> {
                return toContentChunk(buffer, sink);
            }).as(chunks -> {
                return ReactiveRequest.Content.fromPublisher(chunks, getContentType());
            });
            this.builder.content(content);
            sink.success();
        }).then(doCommit());
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMap(Function.identity()).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            DataBufferUtils.release(v0);
        }));
    }

    private String getContentType() {
        MediaType contentType = getHeaders().getContentType();
        return contentType != null ? contentType.toString() : "application/octet-stream";
    }

    private ContentChunk toContentChunk(final DataBuffer buffer, final MonoSink<Void> sink) {
        return new ContentChunk(buffer.asByteBuffer(), new Callback() { // from class: org.springframework.http.client.reactive.JettyClientHttpRequest.1
            public void succeeded() {
                DataBufferUtils.release(buffer);
            }

            public void failed(Throwable t) {
                DataBufferUtils.release(buffer);
                sink.error(t);
            }
        });
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyCookies() {
        Stream map = getCookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).map(cookie -> {
            return new HttpCookie(cookie.getName(), cookie.getValue());
        });
        Request request = this.jettyRequest;
        request.getClass();
        map.forEach(request::cookie);
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        headers.forEach((key, value) -> {
            value.forEach(v -> {
                this.jettyRequest.header(key, v);
            });
        });
        if (!headers.containsKey(HttpHeaders.ACCEPT)) {
            this.jettyRequest.header(HttpHeaders.ACCEPT, "*/*");
        }
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected HttpHeaders initReadOnlyHeaders() {
        MultiValueMap<String, String> jettyHeadersAdapter;
        if (Jetty10HttpFieldsHelper.jetty10Present()) {
            jettyHeadersAdapter = Jetty10HttpFieldsHelper.getHttpHeaders(this.jettyRequest);
        } else {
            jettyHeadersAdapter = new JettyHeadersAdapter(this.jettyRequest.getHeaders());
        }
        MultiValueMap<String, String> headers = jettyHeadersAdapter;
        return HttpHeaders.readOnlyHttpHeaders(headers);
    }

    public ReactiveRequest toReactiveRequest() {
        return this.builder.build();
    }
}
