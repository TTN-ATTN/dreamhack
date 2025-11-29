package org.springframework.web.servlet.function;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ServerResponse;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerResponseBuilder.class */
class DefaultServerResponseBuilder implements ServerResponse.BodyBuilder {
    private final int statusCode;
    private final HttpHeaders headers = new HttpHeaders();
    private final MultiValueMap<String, Cookie> cookies = new LinkedMultiValueMap();

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public /* bridge */ /* synthetic */ ServerResponse.HeadersBuilder allow(Set allowedMethods) {
        return allow((Set<HttpMethod>) allowedMethods);
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public /* bridge */ /* synthetic */ ServerResponse.HeadersBuilder cookies(Consumer cookiesConsumer) {
        return cookies((Consumer<MultiValueMap<String, Cookie>>) cookiesConsumer);
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public /* bridge */ /* synthetic */ ServerResponse.HeadersBuilder headers(Consumer headersConsumer) {
        return headers((Consumer<HttpHeaders>) headersConsumer);
    }

    public DefaultServerResponseBuilder(ServerResponse other) {
        Assert.notNull(other, "ServerResponse must not be null");
        this.statusCode = other instanceof AbstractServerResponse ? ((AbstractServerResponse) other).statusCode : other.statusCode().value();
        this.headers.addAll(other.headers());
        this.cookies.addAll(other.cookies());
    }

    public DefaultServerResponseBuilder(HttpStatus status) {
        Assert.notNull(status, "HttpStatus must not be null");
        this.statusCode = status.value();
    }

    public DefaultServerResponseBuilder(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder header(String headerName, String... headerValues) {
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder cookie(Cookie cookie) {
        Assert.notNull(cookie, "Cookie must not be null");
        this.cookies.add(cookie.getName(), cookie);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder allow(HttpMethod... allowedMethods) {
        this.headers.setAllow(new LinkedHashSet(Arrays.asList(allowedMethods)));
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder allow(Set<HttpMethod> allowedMethods) {
        this.headers.setAllow(allowedMethods);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse.BodyBuilder contentLength(long contentLength) {
        this.headers.setContentLength(contentLength);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse.BodyBuilder contentType(MediaType contentType) {
        this.headers.setContentType(contentType);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder eTag(String etag) {
        if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
            etag = "\"" + etag;
        }
        if (!etag.endsWith("\"")) {
            etag = etag + "\"";
        }
        this.headers.setETag(etag);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder lastModified(ZonedDateTime lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder lastModified(Instant lastModified) {
        this.headers.setLastModified(lastModified);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder location(URI location) {
        this.headers.setLocation(location);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder cacheControl(CacheControl cacheControl) {
        this.headers.setCacheControl(cacheControl);
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse.BodyBuilder varyBy(String... requestHeaders) {
        this.headers.setVary(Arrays.asList(requestHeaders));
        return this;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse build() {
        return build((request, response) -> {
            return null;
        });
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.HeadersBuilder
    public ServerResponse build(BiFunction<HttpServletRequest, HttpServletResponse, ModelAndView> writeFunction) {
        return new WriterFunctionResponse(this.statusCode, this.headers, this.cookies, writeFunction);
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse body(Object body) {
        return DefaultEntityResponseBuilder.fromObject(body).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).build();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public <T> ServerResponse body(T body, ParameterizedTypeReference<T> bodyType) {
        return DefaultEntityResponseBuilder.fromObject(body, bodyType).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).build();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse render(String name, Object... modelAttributes) {
        return new DefaultRenderingResponseBuilder(name).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).modelAttributes(modelAttributes).build();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse.BodyBuilder
    public ServerResponse render(String name, Map<String, ?> model) {
        return new DefaultRenderingResponseBuilder(name).status(this.statusCode).headers(headers -> {
            headers.putAll(this.headers);
        }).cookies(cookies -> {
            cookies.addAll(this.cookies);
        }).modelAttributes(model).build();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerResponseBuilder$WriterFunctionResponse.class */
    private static class WriterFunctionResponse extends AbstractServerResponse {
        private final BiFunction<HttpServletRequest, HttpServletResponse, ModelAndView> writeFunction;

        public WriterFunctionResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, BiFunction<HttpServletRequest, HttpServletResponse, ModelAndView> writeFunction) {
            super(statusCode, headers, cookies);
            Assert.notNull(writeFunction, "WriteFunction must not be null");
            this.writeFunction = writeFunction;
        }

        @Override // org.springframework.web.servlet.function.AbstractServerResponse
        protected ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) {
            return this.writeFunction.apply(request, response);
        }
    }
}
