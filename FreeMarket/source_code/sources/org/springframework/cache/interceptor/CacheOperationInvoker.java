package org.springframework.cache.interceptor;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheOperationInvoker.class */
public interface CacheOperationInvoker {
    @Nullable
    Object invoke() throws ThrowableWrapper;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheOperationInvoker$ThrowableWrapper.class */
    public static class ThrowableWrapper extends RuntimeException {
        private final Throwable original;

        public ThrowableWrapper(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }
}
