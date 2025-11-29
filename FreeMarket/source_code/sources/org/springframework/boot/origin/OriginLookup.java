package org.springframework.boot.origin;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/origin/OriginLookup.class */
public interface OriginLookup<K> {
    Origin getOrigin(K key);

    default boolean isImmutable() {
        return false;
    }

    default String getPrefix() {
        return null;
    }

    static <K> Origin getOrigin(Object source, K key) {
        if (!(source instanceof OriginLookup)) {
            return null;
        }
        try {
            return ((OriginLookup) source).getOrigin(key);
        } catch (Throwable th) {
            return null;
        }
    }
}
