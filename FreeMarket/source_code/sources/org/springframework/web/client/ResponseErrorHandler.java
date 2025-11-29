package org.springframework.web.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/ResponseErrorHandler.class */
public interface ResponseErrorHandler {
    boolean hasError(ClientHttpResponse response) throws IOException;

    void handleError(ClientHttpResponse response) throws IOException;

    default void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        handleError(response);
    }
}
