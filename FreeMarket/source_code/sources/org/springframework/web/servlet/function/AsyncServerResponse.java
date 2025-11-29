package org.springframework.web.servlet.function;

import java.time.Duration;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/AsyncServerResponse.class */
public interface AsyncServerResponse extends ServerResponse {
    ServerResponse block();

    static AsyncServerResponse create(Object asyncResponse) {
        return DefaultAsyncServerResponse.create(asyncResponse, null);
    }

    static AsyncServerResponse create(Object asyncResponse, Duration timeout) {
        return DefaultAsyncServerResponse.create(asyncResponse, timeout);
    }
}
