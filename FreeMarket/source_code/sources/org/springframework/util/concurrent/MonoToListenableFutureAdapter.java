package org.springframework.util.concurrent;

import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/concurrent/MonoToListenableFutureAdapter.class */
public class MonoToListenableFutureAdapter<T> extends CompletableToListenableFutureAdapter<T> {
    public MonoToListenableFutureAdapter(Mono<T> mono) {
        super(mono.toFuture());
    }
}
