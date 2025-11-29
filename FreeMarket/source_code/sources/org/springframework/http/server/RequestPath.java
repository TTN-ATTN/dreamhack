package org.springframework.http.server;

import java.net.URI;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/RequestPath.class */
public interface RequestPath extends PathContainer {
    PathContainer contextPath();

    PathContainer pathWithinApplication();

    RequestPath modifyContextPath(String contextPath);

    static RequestPath parse(URI uri, @Nullable String contextPath) {
        return parse(uri.getRawPath(), contextPath);
    }

    static RequestPath parse(String rawPath, @Nullable String contextPath) {
        return new DefaultRequestPath(rawPath, contextPath);
    }
}
