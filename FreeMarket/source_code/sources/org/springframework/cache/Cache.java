package org.springframework.cache;

import java.util.concurrent.Callable;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/Cache.class */
public interface Cache {

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/Cache$ValueWrapper.class */
    public interface ValueWrapper {
        @Nullable
        Object get();
    }

    String getName();

    Object getNativeCache();

    @Nullable
    ValueWrapper get(Object key);

    @Nullable
    <T> T get(Object key, @Nullable Class<T> type);

    @Nullable
    <T> T get(Object key, Callable<T> valueLoader);

    void put(Object key, @Nullable Object value);

    void evict(Object key);

    void clear();

    @Nullable
    default ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        ValueWrapper existingValue = get(key);
        if (existingValue == null) {
            put(key, value);
        }
        return existingValue;
    }

    default boolean evictIfPresent(Object key) {
        evict(key);
        return false;
    }

    default boolean invalidate() {
        clear();
        return false;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/Cache$ValueRetrievalException.class */
    public static class ValueRetrievalException extends RuntimeException {

        @Nullable
        private final Object key;

        public ValueRetrievalException(@Nullable Object key, Callable<?> loader, Throwable ex) {
            super(String.format("Value for key '%s' could not be loaded using '%s'", key, loader), ex);
            this.key = key;
        }

        @Nullable
        public Object getKey() {
            return this.key;
        }
    }
}
