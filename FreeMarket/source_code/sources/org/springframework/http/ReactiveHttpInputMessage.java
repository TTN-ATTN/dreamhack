package org.springframework.http;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/ReactiveHttpInputMessage.class */
public interface ReactiveHttpInputMessage extends HttpMessage {
    Flux<DataBuffer> getBody();
}
