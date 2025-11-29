package org.springframework.http.codec.multipart;

import java.io.File;
import java.nio.file.Path;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/FilePart.class */
public interface FilePart extends Part {
    String filename();

    Mono<Void> transferTo(Path dest);

    default Mono<Void> transferTo(File dest) {
        return transferTo(dest.toPath());
    }
}
