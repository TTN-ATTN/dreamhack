package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/AsyncClientHttpRequestExecution.class */
public interface AsyncClientHttpRequestExecution {
    ListenableFuture<ClientHttpResponse> executeAsync(HttpRequest request, byte[] body) throws IOException;
}
