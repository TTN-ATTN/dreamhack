package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/support/RootClassFilter.class */
public class RootClassFilter implements ClassFilter, Serializable {
    private final Class<?> clazz;

    public RootClassFilter(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> candidate) {
        return this.clazz.isAssignableFrom(candidate);
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof RootClassFilter) && this.clazz.equals(((RootClassFilter) other).clazz));
    }

    public int hashCode() {
        return this.clazz.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + this.clazz.getName();
    }
}
