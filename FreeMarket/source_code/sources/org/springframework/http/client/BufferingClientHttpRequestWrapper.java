package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/BufferingClientHttpRequestWrapper.class */
final class BufferingClientHttpRequestWrapper extends AbstractBufferingClientHttpRequest {
    private final ClientHttpRequest request;

    BufferingClientHttpRequestWrapper(ClientHttpRequest request) {
        this.request = request;
    }

    @Override // org.springframework.http.HttpRequest
    @Nullable
    public HttpMethod getMethod() {
        return this.request.getMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.request.getMethodValue();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.request.getURI();
    }

    @Override // org.springframework.http.client.AbstractBufferingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        this.request.getHeaders().putAll(headers);
        StreamUtils.copy(bufferedOutput, this.request.getBody());
        ClientHttpResponse response = this.request.execute();
        return new BufferingClientHttpResponseWrapper(response);
    }
}
