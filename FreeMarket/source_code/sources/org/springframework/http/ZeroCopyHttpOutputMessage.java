package org.springframework.http;

import java.io.File;
import java.nio.file.Path;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/ZeroCopyHttpOutputMessage.class */
public interface ZeroCopyHttpOutputMessage extends ReactiveHttpOutputMessage {
    Mono<Void> writeWith(Path file, long position, long count);

    default Mono<Void> writeWith(File file, long position, long count) {
        return writeWith(file.toPath(), position, count);
    }
}
