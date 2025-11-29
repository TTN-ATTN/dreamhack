package org.springframework.http.server.reactive;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.PropertyAccessor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/AbstractServerHttpRequest.class */
public abstract class AbstractServerHttpRequest implements ServerHttpRequest {
    private static final Pattern QUERY_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private final URI uri;
    private final RequestPath path;
    private final HttpHeaders headers;

    @Nullable
    private MultiValueMap<String, String> queryParams;

    @Nullable
    private MultiValueMap<String, HttpCookie> cookies;

    @Nullable
    private SslInfo sslInfo;

    @Nullable
    private String id;

    @Nullable
    private String logPrefix;

    protected abstract MultiValueMap<String, HttpCookie> initCookies();

    @Nullable
    protected abstract SslInfo initSslInfo();

    public abstract <T> T getNativeRequest();

    public AbstractServerHttpRequest(URI uri, @Nullable String contextPath, MultiValueMap<String, String> headers) {
        this.uri = uri;
        this.path = RequestPath.parse(uri, contextPath);
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    public AbstractServerHttpRequest(URI uri, @Nullable String contextPath, HttpHeaders headers) {
        this.uri = uri;
        this.path = RequestPath.parse(uri, contextPath);
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public String getId() {
        if (this.id == null) {
            this.id = initId();
            if (this.id == null) {
                this.id = ObjectUtils.getIdentityHexString(this);
            }
        }
        return this.id;
    }

    @Nullable
    protected String initId() {
        return null;
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public RequestPath getPath() {
        return this.path;
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, String> getQueryParams() {
        if (this.queryParams == null) {
            this.queryParams = CollectionUtils.unmodifiableMultiValueMap(initQueryParams());
        }
        return this.queryParams;
    }

    protected MultiValueMap<String, String> initQueryParams() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        String query = getURI().getRawQuery();
        if (query != null) {
            Matcher matcher = QUERY_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = decodeQueryParam(matcher.group(1));
                String eq = matcher.group(2);
                String value = matcher.group(3);
                queryParams.add(name, value != null ? decodeQueryParam(value) : StringUtils.hasLength(eq) ? "" : null);
            }
        }
        return queryParams;
    }

    private String decodeQueryParam(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return URLDecoder.decode(value);
        }
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, HttpCookie> getCookies() {
        if (this.cookies == null) {
            this.cookies = CollectionUtils.unmodifiableMultiValueMap(initCookies());
        }
        return this.cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public SslInfo getSslInfo() {
        if (this.sslInfo == null) {
            this.sslInfo = initSslInfo();
        }
        return this.sslInfo;
    }

    String getLogPrefix() {
        if (this.logPrefix == null) {
            this.logPrefix = PropertyAccessor.PROPERTY_KEY_PREFIX + initLogPrefix() + "] ";
        }
        return this.logPrefix;
    }

    protected String initLogPrefix() {
        return getId();
    }
}
