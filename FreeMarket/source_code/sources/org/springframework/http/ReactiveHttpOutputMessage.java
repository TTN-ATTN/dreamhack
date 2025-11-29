package org.springframework.http;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/ReactiveHttpOutputMessage.class */
public interface ReactiveHttpOutputMessage extends HttpMessage {
    DataBufferFactory bufferFactory();

    void beforeCommit(Supplier<? extends Mono<Void>> action);

    boolean isCommitted();

    Mono<Void> writeWith(Publisher<? extends DataBuffer> body);

    Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body);

    Mono<Void> setComplete();
}
