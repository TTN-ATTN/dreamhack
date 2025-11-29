package org.springframework.cache.support;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/support/AbstractValueAdaptingCache.class */
public abstract class AbstractValueAdaptingCache implements Cache {
    private final boolean allowNullValues;

    @Nullable
    protected abstract Object lookup(Object key);

    protected AbstractValueAdaptingCache(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public final boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        return toValueWrapper(lookup(key));
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public <T> T get(Object obj, @Nullable Class<T> cls) {
        T t = (T) fromStoreValue(lookup(obj));
        if (t != null && cls != null && !cls.isInstance(t)) {
            throw new IllegalStateException("Cached value is not of required type [" + cls.getName() + "]: " + t);
        }
        return t;
    }

    @Nullable
    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(@Nullable Object userValue) {
        if (userValue == null) {
            if (this.allowNullValues) {
                return NullValue.INSTANCE;
            }
            throw new IllegalArgumentException("Cache '" + getName() + "' is configured to not allow null values but null was provided");
        }
        return userValue;
    }

    @Nullable
    protected Cache.ValueWrapper toValueWrapper(@Nullable Object storeValue) {
        if (storeValue != null) {
            return new SimpleValueWrapper(fromStoreValue(storeValue));
        }
        return null;
    }
}
