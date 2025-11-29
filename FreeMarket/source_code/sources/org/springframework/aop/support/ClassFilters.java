package org.springframework.aop.support;

import java.io.Serializable;
import java.util.Arrays;
import org.springframework.aop.ClassFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/support/ClassFilters.class */
public abstract class ClassFilters {
    public static ClassFilter union(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new UnionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter union(ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new UnionClassFilter(classFilters);
    }

    public static ClassFilter intersection(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new IntersectionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter intersection(ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new IntersectionClassFilter(classFilters);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/support/ClassFilters$UnionClassFilter.class */
    private static class UnionClassFilter implements ClassFilter, Serializable {
        private final ClassFilter[] filters;

        UnionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            for (ClassFilter filter : this.filters) {
                if (filter.matches(clazz)) {
                    return true;
                }
            }
            return false;
        }

        public boolean equals(@Nullable Object other) {
            return this == other || ((other instanceof UnionClassFilter) && ObjectUtils.nullSafeEquals(this.filters, ((UnionClassFilter) other).filters));
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode((Object[]) this.filters);
        }

        public String toString() {
            return getClass().getName() + ": " + Arrays.toString(this.filters);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/support/ClassFilters$IntersectionClassFilter.class */
    private static class IntersectionClassFilter implements ClassFilter, Serializable {
        private final ClassFilter[] filters;

        IntersectionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            for (ClassFilter filter : this.filters) {
                if (!filter.matches(clazz)) {
                    return false;
                }
            }
            return true;
        }

        public boolean equals(@Nullable Object other) {
            return this == other || ((other instanceof IntersectionClassFilter) && ObjectUtils.nullSafeEquals(this.filters, ((IntersectionClassFilter) other).filters));
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode((Object[]) this.filters);
        }

        public String toString() {
            return getClass().getName() + ": " + Arrays.toString(this.filters);
        }
    }
}
