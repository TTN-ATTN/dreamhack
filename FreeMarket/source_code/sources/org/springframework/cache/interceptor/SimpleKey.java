package org.springframework.cache.interceptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/SimpleKey.class */
public class SimpleKey implements Serializable {
    public static final SimpleKey EMPTY = new SimpleKey(new Object[0]);
    private final Object[] params;
    private transient int hashCode;

    public SimpleKey(Object... elements) {
        Assert.notNull(elements, "Elements must not be null");
        this.params = (Object[]) elements.clone();
        this.hashCode = Arrays.deepHashCode(this.params);
    }

    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof SimpleKey) && Arrays.deepEquals(this.params, ((SimpleKey) other).params));
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return getClass().getSimpleName() + " [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.hashCode = Arrays.deepHashCode(this.params);
    }
}
