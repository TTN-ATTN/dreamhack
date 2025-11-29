package org.springframework.http;

import java.net.URI;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/HttpRequest.class */
public interface HttpRequest extends HttpMessage {
    String getMethodValue();

    URI getURI();

    @Nullable
    default HttpMethod getMethod() {
        return HttpMethod.resolve(getMethodValue());
    }
}
