package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.AsyncClientHttpRequest;

@FunctionalInterface
@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/AsyncRequestCallback.class */
public interface AsyncRequestCallback {
    void doWithRequest(AsyncClientHttpRequest request) throws IOException;
}
