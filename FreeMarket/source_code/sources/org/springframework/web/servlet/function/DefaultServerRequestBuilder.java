package org.springframework.web.servlet.function;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.servlet.function.DefaultServerRequest;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequestBuilder.class */
class DefaultServerRequestBuilder implements ServerRequest.Builder {
    private final HttpServletRequest servletRequest;
    private final List<HttpMessageConverter<?>> messageConverters;
    private String methodName;
    private URI uri;

    @Nullable
    private InetSocketAddress remoteAddress;
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap();
    private final Map<String, Object> attributes = new LinkedHashMap();
    private final MultiValueMap<String, String> params = new LinkedMultiValueMap();
    private byte[] body = new byte[0];

    public DefaultServerRequestBuilder(ServerRequest other) {
        Assert.notNull(other, "ServerRequest must not be null");
        this.servletRequest = other.servletRequest();
        this.messageConverters = new ArrayList(other.messageConverters());
        this.methodName = other.methodName();
        this.uri = other.uri();
        headers(headers -> {
            headers.addAll(other.headers().asHttpHeaders());
        });
        cookies(cookies -> {
            cookies.addAll(other.cookies());
        });
        attributes(attributes -> {
            attributes.putAll(other.attributes());
        });
        params(params -> {
            params.addAll(other.params());
        });
        this.remoteAddress = other.remoteAddress().orElse(null);
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder method(HttpMethod method) {
        Assert.notNull(method, "HttpMethod must not be null");
        this.methodName = method.name();
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder uri(URI uri) {
        Assert.notNull(uri, "URI must not be null");
        this.uri = uri;
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder header(String headerName, String... headerValues) {
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder headers(Consumer<HttpHeaders> headersConsumer) {
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder cookie(String name, String... values) {
        for (String value : values) {
            this.cookies.add(name, new Cookie(name, value));
        }
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder body(byte[] body) {
        this.body = body;
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder body(String body) {
        return body(body.getBytes(StandardCharsets.UTF_8));
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder attribute(String name, Object value) {
        Assert.notNull(name, "'name' must not be null");
        this.attributes.put(name, value);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder attributes(Consumer<Map<String, Object>> attributesConsumer) {
        attributesConsumer.accept(this.attributes);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder param(String name, String... values) {
        for (String value : values) {
            this.params.add(name, value);
        }
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder params(Consumer<MultiValueMap<String, String>> paramsConsumer) {
        paramsConsumer.accept(this.params);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest.Builder remoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest.Builder
    public ServerRequest build() {
        return new BuiltServerRequest(this.servletRequest, this.methodName, this.uri, this.headers, this.cookies, this.attributes, this.params, this.remoteAddress, this.body, this.messageConverters);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequestBuilder$BuiltServerRequest.class */
    private static class BuiltServerRequest implements ServerRequest {
        private final String methodName;
        private final URI uri;
        private final HttpHeaders headers;
        private final HttpServletRequest servletRequest;
        private final MultiValueMap<String, Cookie> cookies;
        private final Map<String, Object> attributes;
        private final byte[] body;
        private final List<HttpMessageConverter<?>> messageConverters;
        private final MultiValueMap<String, String> params;

        @Nullable
        private final InetSocketAddress remoteAddress;

        public BuiltServerRequest(HttpServletRequest servletRequest, String methodName, URI uri, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, Map<String, Object> attributes, MultiValueMap<String, String> params, @Nullable InetSocketAddress remoteAddress, byte[] body, List<HttpMessageConverter<?>> messageConverters) {
            this.servletRequest = servletRequest;
            this.methodName = methodName;
            this.uri = uri;
            this.headers = new HttpHeaders(headers);
            this.cookies = new LinkedMultiValueMap(cookies);
            this.attributes = new LinkedHashMap(attributes);
            this.params = new LinkedMultiValueMap(params);
            this.remoteAddress = remoteAddress;
            this.body = body;
            this.messageConverters = messageConverters;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public String methodName() {
            return this.methodName;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, Part> multipartData() throws ServletException, IOException {
            return (MultiValueMap) servletRequest().getParts().stream().collect(Collectors.groupingBy((v0) -> {
                return v0.getName();
            }, LinkedMultiValueMap::new, Collectors.toList()));
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public URI uri() {
            return this.uri;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public UriBuilder uriBuilder() {
            return UriComponentsBuilder.fromUri(this.uri);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public ServerRequest.Headers headers() {
            return new DefaultServerRequest.DefaultRequestHeaders(this.headers);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, Cookie> cookies() {
            return this.cookies;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<InetSocketAddress> remoteAddress() {
            return Optional.ofNullable(this.remoteAddress);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public List<HttpMessageConverter<?>> messageConverters() {
            return this.messageConverters;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T body(Class<T> cls) throws ServletException, IOException {
            return (T) bodyInternal(cls, cls);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T body(ParameterizedTypeReference<T> parameterizedTypeReference) throws ServletException, IOException {
            Type type = parameterizedTypeReference.getType();
            return (T) bodyInternal(type, DefaultServerRequest.bodyClass(type));
        }

        /* JADX WARN: Type inference failed for: r0v28, types: [T, java.lang.Object] */
        private <T> T bodyInternal(Type type, Class<?> cls) throws ServletException, IOException {
            BuiltInputMessage builtInputMessage = new BuiltInputMessage();
            MediaType mediaTypeOrElse = headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
            for (HttpMessageConverter<?> httpMessageConverter : this.messageConverters) {
                if (httpMessageConverter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter genericHttpMessageConverter = (GenericHttpMessageConverter) httpMessageConverter;
                    if (genericHttpMessageConverter.canRead(type, cls, mediaTypeOrElse)) {
                        return genericHttpMessageConverter.read(type, cls, builtInputMessage);
                    }
                }
                if (httpMessageConverter.canRead(cls, mediaTypeOrElse)) {
                    return (T) httpMessageConverter.read2(cls, builtInputMessage);
                }
            }
            throw new HttpMediaTypeNotSupportedException(mediaTypeOrElse, Collections.emptyList());
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Map<String, Object> attributes() {
            return this.attributes;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, String> params() {
            return this.params;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Map<String, String> pathVariables() {
            Map<String, String> pathVariables = (Map) attributes().get(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathVariables != null) {
                return pathVariables;
            }
            return Collections.emptyMap();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpSession session() {
            return this.servletRequest.getSession();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<Principal> principal() {
            return Optional.ofNullable(this.servletRequest.getUserPrincipal());
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpServletRequest servletRequest() {
            return this.servletRequest;
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequestBuilder$BuiltServerRequest$BuiltInputMessage.class */
        private class BuiltInputMessage implements HttpInputMessage {
            private BuiltInputMessage() {
            }

            @Override // org.springframework.http.HttpInputMessage
            public InputStream getBody() throws IOException {
                return new BodyInputStream(BuiltServerRequest.this.body);
            }

            @Override // org.springframework.http.HttpMessage
            public HttpHeaders getHeaders() {
                return BuiltServerRequest.this.headers;
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequestBuilder$BodyInputStream.class */
    private static class BodyInputStream extends ServletInputStream {
        private final InputStream delegate;

        public BodyInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override // javax.servlet.ServletInputStream
        public boolean isFinished() {
            return false;
        }

        @Override // javax.servlet.ServletInputStream
        public boolean isReady() {
            return true;
        }

        @Override // javax.servlet.ServletInputStream
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override // java.io.InputStream
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override // java.io.InputStream
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override // java.io.InputStream
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}
