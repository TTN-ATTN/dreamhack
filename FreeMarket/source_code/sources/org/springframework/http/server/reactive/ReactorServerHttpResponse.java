package org.springframework.http.server.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelId;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ChannelOperationsId;
import reactor.netty.http.server.HttpServerResponse;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ReactorServerHttpResponse.class */
class ReactorServerHttpResponse extends AbstractServerHttpResponse implements ZeroCopyHttpOutputMessage {
    private static final Log logger = LogFactory.getLog((Class<?>) ReactorServerHttpResponse.class);
    private final HttpServerResponse response;

    public ReactorServerHttpResponse(HttpServerResponse response, DataBufferFactory bufferFactory) {
        super(bufferFactory, new HttpHeaders(new NettyHeadersAdapter(response.responseHeaders())));
        Assert.notNull(response, "HttpServerResponse must not be null");
        this.response = response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    public HttpStatus getStatusCode() {
        HttpStatus status = super.getStatusCode();
        return status != null ? status : HttpStatus.resolve(this.response.status().code());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return Integer.valueOf(status != null ? status.intValue() : this.response.status().code());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        Integer status = super.getRawStatusCode();
        if (status != null) {
            this.response.status(status.intValue());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
        return this.response.send(toByteBufs(publisher)).then();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> publisher) {
        return this.response.sendGroups(Flux.from(publisher).map(this::toByteBufs)).then();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        Iterator<ResponseCookie> it = getCookies().values().iterator();
        while (it.hasNext()) {
            List<ResponseCookie> cookies = (List) it.next();
            for (ResponseCookie cookie : cookies) {
                this.response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }
        }
    }

    @Override // org.springframework.http.ZeroCopyHttpOutputMessage
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() -> {
            return this.response.sendFile(file, position, count).then();
        });
    }

    private Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        if (dataBuffers instanceof Mono) {
            return Mono.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
        }
        return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void touchDataBuffer(DataBuffer buffer) {
        if (logger.isDebugEnabled()) {
            if (ReactorServerHttpRequest.reactorNettyRequestChannelOperationsIdPresent && ChannelOperationsIdHelper.touch(buffer, this.response)) {
                return;
            }
            this.response.withConnection(connection -> {
                ChannelId id = connection.channel().id();
                DataBufferUtils.touch(buffer, "Channel id: " + id.asShortText());
            });
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ReactorServerHttpResponse$ChannelOperationsIdHelper.class */
    private static class ChannelOperationsIdHelper {
        private ChannelOperationsIdHelper() {
        }

        public static boolean touch(DataBuffer dataBuffer, HttpServerResponse response) {
            if (response instanceof ChannelOperationsId) {
                String id = ((ChannelOperationsId) response).asLongText();
                DataBufferUtils.touch(dataBuffer, "Channel id: " + id);
                return true;
            }
            return false;
        }
    }
}
