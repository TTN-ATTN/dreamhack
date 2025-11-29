package org.springframework.web.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/MethodNotAllowedException.class */
public class MethodNotAllowedException extends ResponseStatusException {
    private final String method;
    private final Set<HttpMethod> httpMethods;

    public MethodNotAllowedException(HttpMethod method, Collection<HttpMethod> supportedMethods) {
        this(method.name(), supportedMethods);
    }

    public MethodNotAllowedException(String method, @Nullable Collection<HttpMethod> supportedMethods) {
        super(HttpStatus.METHOD_NOT_ALLOWED, "Request method '" + method + "' not supported");
        Assert.notNull(method, "'method' is required");
        supportedMethods = supportedMethods == null ? Collections.emptySet() : supportedMethods;
        this.method = method;
        this.httpMethods = Collections.unmodifiableSet(new LinkedHashSet(supportedMethods));
    }

    @Override // org.springframework.web.server.ResponseStatusException
    public Map<String, String> getHeaders() {
        return getResponseHeaders().toSingleValueMap();
    }

    @Override // org.springframework.web.server.ResponseStatusException
    public HttpHeaders getResponseHeaders() {
        if (CollectionUtils.isEmpty(this.httpMethods)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(this.httpMethods);
        return headers;
    }

    public String getHttpMethod() {
        return this.method;
    }

    public Set<HttpMethod> getSupportedMethods() {
        return this.httpMethods;
    }
}
