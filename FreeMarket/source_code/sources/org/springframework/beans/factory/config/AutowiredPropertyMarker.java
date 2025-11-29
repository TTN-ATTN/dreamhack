package org.springframework.beans.factory.config;

import java.io.Serializable;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/config/AutowiredPropertyMarker.class */
public final class AutowiredPropertyMarker implements Serializable {
    public static final Object INSTANCE = new AutowiredPropertyMarker();

    private AutowiredPropertyMarker() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return AutowiredPropertyMarker.class.hashCode();
    }

    public String toString() {
        return "(autowired)";
    }
}
