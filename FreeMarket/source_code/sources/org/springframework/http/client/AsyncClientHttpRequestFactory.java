package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/client/AsyncClientHttpRequestFactory.class */
public interface AsyncClientHttpRequestFactory {
    AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException;
}
