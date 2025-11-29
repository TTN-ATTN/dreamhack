package org.springframework.cglib.core.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/core/internal/LoadingCache.class */
public class LoadingCache<K, KK, V> {
    protected final ConcurrentMap<KK, Object> map = new ConcurrentHashMap();
    protected final Function<K, V> loader;
    protected final Function<K, KK> keyMapper;
    public static final Function IDENTITY = new Function() { // from class: org.springframework.cglib.core.internal.LoadingCache.1
        @Override // org.springframework.cglib.core.internal.Function
        public Object apply(Object key) {
            return key;
        }
    };

    public LoadingCache(Function<K, KK> keyMapper, Function<K, V> loader) {
        this.keyMapper = keyMapper;
        this.loader = loader;
    }

    public static <K> Function<K, K> identity() {
        return IDENTITY;
    }

    public V get(K k) {
        KK kkApply = this.keyMapper.apply(k);
        V v = (V) this.map.get(kkApply);
        if (v != null && !(v instanceof FutureTask)) {
            return v;
        }
        return createEntry(k, kkApply, v);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [V, java.lang.Object] */
    protected V createEntry(final K k, KK kk, Object obj) throws ExecutionException, InterruptedException {
        FutureTask futureTask;
        boolean z = false;
        if (obj != null) {
            futureTask = (FutureTask) obj;
        } else {
            futureTask = new FutureTask(new Callable<V>() { // from class: org.springframework.cglib.core.internal.LoadingCache.2
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.util.concurrent.Callable
                public V call() throws Exception {
                    return (V) LoadingCache.this.loader.apply(k);
                }
            });
            V v = (V) this.map.putIfAbsent(kk, futureTask);
            if (v == 0) {
                z = true;
                futureTask.run();
            } else if (v instanceof FutureTask) {
                futureTask = (FutureTask) v;
            } else {
                return v;
            }
        }
        try {
            ?? r0 = futureTask.get();
            if (z) {
                this.map.put(kk, r0);
            }
            return r0;
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while loading cache item", e);
        } catch (ExecutionException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            throw new IllegalStateException("Unable to load cache item", cause);
        }
    }
}
