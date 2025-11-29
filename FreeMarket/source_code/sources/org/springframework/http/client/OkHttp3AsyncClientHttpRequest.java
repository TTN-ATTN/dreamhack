package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/OkHttp3AsyncClientHttpRequest.class */
class OkHttp3AsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest {
    private final OkHttpClient client;
    private final URI uri;
    private final HttpMethod method;

    public OkHttp3AsyncClientHttpRequest(OkHttpClient client, URI uri, HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.method.name();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] content) throws IOException {
        Request request = OkHttp3ClientHttpRequestFactory.buildRequest(headers, content, this.uri, this.method);
        return new OkHttpListenableFuture(this.client.newCall(request));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/OkHttp3AsyncClientHttpRequest$OkHttpListenableFuture.class */
    private static class OkHttpListenableFuture extends SettableListenableFuture<ClientHttpResponse> {
        private final Call call;

        public OkHttpListenableFuture(Call call) {
            this.call = call;
            this.call.enqueue(new Callback() { // from class: org.springframework.http.client.OkHttp3AsyncClientHttpRequest.OkHttpListenableFuture.1
                public void onResponse(Call call2, Response response) {
                    OkHttpListenableFuture.this.set(new OkHttp3ClientHttpResponse(response));
                }

                public void onFailure(Call call2, IOException ex) {
                    OkHttpListenableFuture.this.setException(ex);
                }
            });
        }

        @Override // org.springframework.util.concurrent.SettableListenableFuture
        protected void interruptTask() {
            this.call.cancel();
        }
    }
}
