package org.springframework.web.servlet.function;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UriBuilder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequest.class */
class DefaultServerRequest implements ServerRequest {
    private final ServletServerHttpRequest serverHttpRequest;
    private final RequestPath requestPath;
    private final ServerRequest.Headers headers;
    private final List<HttpMessageConverter<?>> messageConverters;
    private final MultiValueMap<String, String> params;
    private final Map<String, Object> attributes;

    @Nullable
    private MultiValueMap<String, Part> parts;

    public DefaultServerRequest(HttpServletRequest servletRequest, List<HttpMessageConverter<?>> messageConverters) {
        RequestPath andCache;
        this.serverHttpRequest = new ServletServerHttpRequest(servletRequest);
        this.messageConverters = Collections.unmodifiableList(new ArrayList(messageConverters));
        this.headers = new DefaultRequestHeaders(this.serverHttpRequest.getHeaders());
        this.params = CollectionUtils.toMultiValueMap(new ServletParametersMap(servletRequest));
        this.attributes = new ServletAttributesMap(servletRequest);
        if (ServletRequestPathUtils.hasParsedRequestPath(servletRequest)) {
            andCache = ServletRequestPathUtils.getParsedRequestPath(servletRequest);
        } else {
            andCache = ServletRequestPathUtils.parseAndCache(servletRequest);
        }
        this.requestPath = andCache;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public String methodName() {
        return servletRequest().getMethod();
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public URI uri() {
        return this.serverHttpRequest.getURI();
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public UriBuilder uriBuilder() {
        return ServletUriComponentsBuilder.fromRequest(servletRequest());
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public RequestPath requestPath() {
        return this.requestPath;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public ServerRequest.Headers headers() {
        return this.headers;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public MultiValueMap<String, Cookie> cookies() {
        Cookie[] cookies = servletRequest().getCookies();
        if (cookies == null) {
            cookies = new Cookie[0];
        }
        MultiValueMap<String, Cookie> result = new LinkedMultiValueMap<>(cookies.length);
        for (Cookie cookie : cookies) {
            result.add(cookie.getName(), cookie);
        }
        return result;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public HttpServletRequest servletRequest() {
        return this.serverHttpRequest.getServletRequest();
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public Optional<InetSocketAddress> remoteAddress() {
        return Optional.of(this.serverHttpRequest.getRemoteAddress());
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
        return (T) bodyInternal(type, bodyClass(type));
    }

    static Class<?> bodyClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class) {
                return (Class) parameterizedType.getRawType();
            }
            return Object.class;
        }
        return Object.class;
    }

    /* JADX WARN: Type inference failed for: r0v27, types: [T, java.lang.Object] */
    private <T> T bodyInternal(Type type, Class<?> cls) throws ServletException, IOException {
        MediaType mediaTypeOrElse = this.headers.contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
        for (HttpMessageConverter<?> httpMessageConverter : this.messageConverters) {
            if (httpMessageConverter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter genericHttpMessageConverter = (GenericHttpMessageConverter) httpMessageConverter;
                if (genericHttpMessageConverter.canRead(type, cls, mediaTypeOrElse)) {
                    return genericHttpMessageConverter.read(type, cls, this.serverHttpRequest);
                }
            }
            if (httpMessageConverter.canRead(cls, mediaTypeOrElse)) {
                return (T) httpMessageConverter.read2(cls, this.serverHttpRequest);
            }
        }
        throw new HttpMediaTypeNotSupportedException(mediaTypeOrElse, getSupportedMediaTypes(cls));
    }

    private List<MediaType> getSupportedMediaTypes(Class<?> bodyClass) {
        return (List) this.messageConverters.stream().flatMap(converter -> {
            return converter.getSupportedMediaTypes(bodyClass).stream();
        }).sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public Optional<Object> attribute(String name) {
        return Optional.ofNullable(servletRequest().getAttribute(name));
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public Map<String, Object> attributes() {
        return this.attributes;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public Optional<String> param(String name) {
        return Optional.ofNullable(servletRequest().getParameter(name));
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public MultiValueMap<String, String> params() {
        return this.params;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public MultiValueMap<String, Part> multipartData() throws ServletException, IOException {
        MultiValueMap<String, Part> result = this.parts;
        if (result == null) {
            result = (MultiValueMap) servletRequest().getParts().stream().collect(Collectors.groupingBy((v0) -> {
                return v0.getName();
            }, LinkedMultiValueMap::new, Collectors.toList()));
            this.parts = result;
        }
        return result;
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public Map<String, String> pathVariables() {
        Map<String, String> pathVariables = (Map) servletRequest().getAttribute(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null) {
            return pathVariables;
        }
        return Collections.emptyMap();
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public HttpSession session() {
        return servletRequest().getSession(true);
    }

    @Override // org.springframework.web.servlet.function.ServerRequest
    public Optional<Principal> principal() {
        return Optional.ofNullable(this.serverHttpRequest.getPrincipal());
    }

    public String toString() {
        return String.format("HTTP %s %s", method(), path());
    }

    static Optional<ServerResponse> checkNotModified(HttpServletRequest servletRequest, @Nullable Instant lastModified, @Nullable String etag) {
        long lastModifiedTimestamp = -1;
        if (lastModified != null && lastModified.isAfter(Instant.EPOCH)) {
            lastModifiedTimestamp = lastModified.toEpochMilli();
        }
        CheckNotModifiedResponse response = new CheckNotModifiedResponse();
        WebRequest webRequest = new ServletWebRequest(servletRequest, response);
        if (webRequest.checkNotModified(etag, lastModifiedTimestamp)) {
            return Optional.of(ServerResponse.status(response.status).headers(headers -> {
                headers.addAll(response.headers);
            }).build());
        }
        return Optional.empty();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequest$DefaultRequestHeaders.class */
    static class DefaultRequestHeaders implements ServerRequest.Headers {
        private final HttpHeaders httpHeaders;

        public DefaultRequestHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = HttpHeaders.readOnlyHttpHeaders(httpHeaders);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public List<MediaType> accept() {
            return this.httpHeaders.getAccept();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public List<Charset> acceptCharset() {
            return this.httpHeaders.getAcceptCharset();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public List<Locale.LanguageRange> acceptLanguage() {
            return this.httpHeaders.getAcceptLanguage();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public OptionalLong contentLength() {
            long value = this.httpHeaders.getContentLength();
            return value != -1 ? OptionalLong.of(value) : OptionalLong.empty();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public Optional<MediaType> contentType() {
            return Optional.ofNullable(this.httpHeaders.getContentType());
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public InetSocketAddress host() {
            return this.httpHeaders.getHost();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public List<HttpRange> range() {
            return this.httpHeaders.getRange();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public List<String> header(String headerName) {
            List<String> headerValues = this.httpHeaders.get((Object) headerName);
            return headerValues != null ? headerValues : Collections.emptyList();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest.Headers
        public HttpHeaders asHttpHeaders() {
            return this.httpHeaders;
        }

        public String toString() {
            return this.httpHeaders.toString();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequest$ServletParametersMap.class */
    private static final class ServletParametersMap extends AbstractMap<String, List<String>> {
        private final HttpServletRequest servletRequest;

        private ServletParametersMap(HttpServletRequest servletRequest) {
            this.servletRequest = servletRequest;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<String, List<String>>> entrySet() {
            return (Set) this.servletRequest.getParameterMap().entrySet().stream().map(entry -> {
                List<String> value = Arrays.asList((Object[]) entry.getValue());
                return new AbstractMap.SimpleImmutableEntry(entry.getKey(), value);
            }).collect(Collectors.toSet());
        }

        @Override // java.util.AbstractMap, java.util.Map
        public int size() {
            return this.servletRequest.getParameterMap().size();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public List<String> get(Object key) {
            String name = (String) key;
            String[] parameterValues = this.servletRequest.getParameterValues(name);
            if (!ObjectUtils.isEmpty((Object[]) parameterValues)) {
                return Arrays.asList(parameterValues);
            }
            return Collections.emptyList();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public List<String> put(String key, List<String> value) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public List<String> remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequest$ServletAttributesMap.class */
    private static final class ServletAttributesMap extends AbstractMap<String, Object> {
        private final HttpServletRequest servletRequest;

        private ServletAttributesMap(HttpServletRequest servletRequest) {
            this.servletRequest = servletRequest;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            String name = (String) key;
            return this.servletRequest.getAttribute(name) != null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public void clear() {
            List<String> attributeNames = Collections.list(this.servletRequest.getAttributeNames());
            HttpServletRequest httpServletRequest = this.servletRequest;
            httpServletRequest.getClass();
            attributeNames.forEach(httpServletRequest::removeAttribute);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<String, Object>> entrySet() {
            return (Set) Collections.list(this.servletRequest.getAttributeNames()).stream().map(name -> {
                Object value = this.servletRequest.getAttribute(name);
                return new AbstractMap.SimpleImmutableEntry(name, value);
            }).collect(Collectors.toSet());
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Object get(Object key) {
            String name = (String) key;
            return this.servletRequest.getAttribute(name);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Object put(String key, Object value) {
            Object oldValue = this.servletRequest.getAttribute(key);
            this.servletRequest.setAttribute(key, value);
            return oldValue;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Object remove(Object key) {
            String name = (String) key;
            Object value = this.servletRequest.getAttribute(name);
            this.servletRequest.removeAttribute(name);
            return value;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/DefaultServerRequest$CheckNotModifiedResponse.class */
    private static final class CheckNotModifiedResponse implements HttpServletResponse {
        private final HttpHeaders headers;
        private int status;

        private CheckNotModifiedResponse() {
            this.headers = new HttpHeaders();
            this.status = 200;
        }

        @Override // javax.servlet.http.HttpServletResponse
        public boolean containsHeader(String name) {
            return this.headers.containsKey(name);
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void setDateHeader(String name, long date) {
            this.headers.setDate(name, date);
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void setHeader(String name, String value) {
            this.headers.set(name, value);
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void addHeader(String name, String value) {
            this.headers.add(name, value);
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void setStatus(int sc) {
            this.status = sc;
        }

        @Override // javax.servlet.http.HttpServletResponse
        @Deprecated
        public void setStatus(int sc, String sm) {
            this.status = sc;
        }

        @Override // javax.servlet.http.HttpServletResponse
        public int getStatus() {
            return this.status;
        }

        @Override // javax.servlet.http.HttpServletResponse
        @Nullable
        public String getHeader(String name) {
            return this.headers.getFirst(name);
        }

        @Override // javax.servlet.http.HttpServletResponse
        public Collection<String> getHeaders(String name) {
            List<String> result = this.headers.get((Object) name);
            return result != null ? result : Collections.emptyList();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public Collection<String> getHeaderNames() {
            return this.headers.keySet();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void addCookie(Cookie cookie) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public String encodeURL(String url) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public String encodeRedirectURL(String url) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        @Deprecated
        public String encodeUrl(String url) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        @Deprecated
        public String encodeRedirectUrl(String url) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void sendError(int sc, String msg) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void sendError(int sc) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void sendRedirect(String location) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void addDateHeader(String name, long date) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void setIntHeader(String name, int value) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.http.HttpServletResponse
        public void addIntHeader(String name, int value) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public String getCharacterEncoding() {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public String getContentType() {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public ServletOutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public PrintWriter getWriter() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void setCharacterEncoding(String charset) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void setContentLength(int len) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void setContentLengthLong(long len) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void setContentType(String type) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void setBufferSize(int size) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public int getBufferSize() {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void flushBuffer() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void resetBuffer() {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public boolean isCommitted() {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void reset() {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public void setLocale(Locale loc) {
            throw new UnsupportedOperationException();
        }

        @Override // javax.servlet.ServletResponse
        public Locale getLocale() {
            throw new UnsupportedOperationException();
        }
    }
}
