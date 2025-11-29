package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/StreamingHttpOutputMessage.class */
public interface StreamingHttpOutputMessage extends HttpOutputMessage {

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/StreamingHttpOutputMessage$Body.class */
    public interface Body {
        void writeTo(OutputStream outputStream) throws IOException;
    }

    void setBody(Body body);
}
