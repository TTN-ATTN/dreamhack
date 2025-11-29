package org.springframework.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/concurrent/CompletableToListenableFutureAdapter.class */
public class CompletableToListenableFutureAdapter<T> implements ListenableFuture<T> {
    private final CompletableFuture<T> completableFuture;
    private final ListenableFutureCallbackRegistry<T> callbacks;

    public CompletableToListenableFutureAdapter(CompletionStage<T> completionStage) {
        this((CompletableFuture) completionStage.toCompletableFuture());
    }

    public CompletableToListenableFutureAdapter(CompletableFuture<T> completableFuture) {
        this.callbacks = new ListenableFutureCallbackRegistry<>();
        this.completableFuture = completableFuture;
        this.completableFuture.whenComplete((BiConsumer) (result, ex) -> {
            if (ex != null) {
                this.callbacks.failure(ex);
            } else {
                this.callbacks.success(result);
            }
        });
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(ListenableFutureCallback<? super T> callback) {
        this.callbacks.addCallback(callback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
        this.callbacks.addSuccessCallback(successCallback);
        this.callbacks.addFailureCallback(failureCallback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public CompletableFuture<T> completable() {
        return this.completableFuture;
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.completableFuture.cancel(mayInterruptIfRunning);
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return this.completableFuture.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return this.completableFuture.isDone();
    }

    @Override // java.util.concurrent.Future
    public T get() throws ExecutionException, InterruptedException {
        return this.completableFuture.get();
    }

    @Override // java.util.concurrent.Future
    public T get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        return this.completableFuture.get(timeout, unit);
    }
}
