package org.springframework.util.concurrent;

import java.util.concurrent.ExecutionException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/concurrent/ListenableFutureAdapter.class */
public abstract class ListenableFutureAdapter<T, S> extends FutureAdapter<T, S> implements ListenableFuture<T> {
    protected ListenableFutureAdapter(ListenableFuture<S> adaptee) {
        super(adaptee);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(final ListenableFutureCallback<? super T> callback) {
        addCallback(callback, callback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(final SuccessCallback<? super T> successCallback, final FailureCallback failureCallback) {
        ListenableFuture<S> listenableAdaptee = (ListenableFuture) getAdaptee();
        listenableAdaptee.addCallback(new ListenableFutureCallback<S>() { // from class: org.springframework.util.concurrent.ListenableFutureAdapter.1
            @Override // org.springframework.util.concurrent.SuccessCallback
            public void onSuccess(@Nullable S result) {
                T adapted = null;
                if (result != null) {
                    try {
                        adapted = ListenableFutureAdapter.this.adaptInternal(result);
                    } catch (ExecutionException ex) {
                        Throwable cause = ex.getCause();
                        onFailure(cause != null ? cause : ex);
                        return;
                    } catch (Throwable ex2) {
                        onFailure(ex2);
                        return;
                    }
                }
                successCallback.onSuccess(adapted);
            }

            @Override // org.springframework.util.concurrent.FailureCallback
            public void onFailure(Throwable ex) {
                failureCallback.onFailure(ex);
            }
        });
    }
}
