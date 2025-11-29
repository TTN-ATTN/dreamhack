package org.springframework.http.server;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/ServerHttpResponse.class */
public interface ServerHttpResponse extends HttpOutputMessage, Flushable, Closeable {
    void setStatusCode(HttpStatus status);

    void flush() throws IOException;

    void close();
}
