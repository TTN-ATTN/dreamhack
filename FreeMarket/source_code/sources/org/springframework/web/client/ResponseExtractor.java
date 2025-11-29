package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/client/ResponseExtractor.class */
public interface ResponseExtractor<T> {
    @Nullable
    T extractData(ClientHttpResponse response) throws IOException;
}
