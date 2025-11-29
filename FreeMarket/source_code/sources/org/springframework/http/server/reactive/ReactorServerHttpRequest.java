package org.springframework.http.server.reactive;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.ssl.SslHandler;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLSession;
import org.apache.commons.logging.Log;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpLogging;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.ChannelOperationsId;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ReactorServerHttpRequest.class */
class ReactorServerHttpRequest extends AbstractServerHttpRequest {
    static final boolean reactorNettyRequestChannelOperationsIdPresent = ClassUtils.isPresent("reactor.netty.ChannelOperationsId", ReactorServerHttpRequest.class.getClassLoader());
    private static final Log logger = HttpLogging.forLogName(ReactorServerHttpRequest.class);
    private static final AtomicLong logPrefixIndex = new AtomicLong();
    private final HttpServerRequest request;
    private final NettyDataBufferFactory bufferFactory;

    public ReactorServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory bufferFactory) throws URISyntaxException {
        super(initUri(request), "", new NettyHeadersAdapter(request.requestHeaders()));
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull(request, "HttpServerRequest must not be null");
        return new URI(resolveBaseUrl(request) + resolveRequestUri(request));
    }

    private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
        String scheme = request.scheme();
        int port = request.hostPort();
        if (usePort(scheme, port)) {
            return new URI(scheme, null, request.hostName(), port, null, null, null);
        }
        return new URI(scheme, request.hostName(), null, null);
    }

    private static boolean usePort(String scheme, int port) {
        return ((scheme.equals("http") || scheme.equals("ws")) && port != 80) || ((scheme.equals("https") || scheme.equals("wss")) && port != 443);
    }

    private static String resolveRequestUri(HttpServerRequest request) {
        char c;
        String uri = request.uri();
        for (int i = 0; i < uri.length() && (c = uri.charAt(i)) != '/' && c != '?' && c != '#'; i++) {
            if (c == ':' && i + 2 < uri.length() && uri.charAt(i + 1) == '/' && uri.charAt(i + 2) == '/') {
                for (int j = i + 3; j < uri.length(); j++) {
                    char c2 = uri.charAt(j);
                    if (c2 == '/' || c2 == '?' || c2 == '#') {
                        return uri.substring(j);
                    }
                }
                return "";
            }
        }
        return uri;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.request.method().name();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (CharSequence name : this.request.cookies().keySet()) {
            for (Cookie cookie : (Set) this.request.cookies().get(name)) {
                HttpCookie httpCookie = new HttpCookie(name.toString(), cookie.value());
                cookies.add(name.toString(), httpCookie);
            }
        }
        return cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.request.hostAddress();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.request.remoteAddress();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        Channel channel = this.request.channel();
        SslHandler sslHandler = channel.pipeline().get(SslHandler.class);
        if (sslHandler == null && channel.parent() != null) {
            sslHandler = (SslHandler) channel.parent().pipeline().get(SslHandler.class);
        }
        if (sslHandler != null) {
            SSLSession session = sslHandler.engine().getSession();
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        ByteBufFlux byteBufFluxRetain = this.request.receive().retain();
        NettyDataBufferFactory nettyDataBufferFactory = this.bufferFactory;
        nettyDataBufferFactory.getClass();
        return byteBufFluxRetain.map(nettyDataBufferFactory::wrap);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected String initId() {
        if (this.request instanceof Connection) {
            return this.request.channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return null;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected String initLogPrefix() {
        String id;
        if (reactorNettyRequestChannelOperationsIdPresent && (id = ChannelOperationsIdHelper.getId(this.request)) != null) {
            return id;
        }
        if (this.request instanceof Connection) {
            return this.request.channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return getId();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/ReactorServerHttpRequest$ChannelOperationsIdHelper.class */
    private static class ChannelOperationsIdHelper {
        private ChannelOperationsIdHelper() {
        }

        @Nullable
        public static String getId(HttpServerRequest request) {
            if (request instanceof ChannelOperationsId) {
                if (ReactorServerHttpRequest.logger.isDebugEnabled()) {
                    return ((ChannelOperationsId) request).asLongText();
                }
                return ((ChannelOperationsId) request).asShortText();
            }
            return null;
        }
    }
}
