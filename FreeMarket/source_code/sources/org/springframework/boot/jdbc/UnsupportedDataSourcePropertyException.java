package org.springframework.boot.jdbc;

import java.util.function.Supplier;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/UnsupportedDataSourcePropertyException.class */
public class UnsupportedDataSourcePropertyException extends RuntimeException {
    UnsupportedDataSourcePropertyException(String message) {
        super(message);
    }

    static void throwIf(boolean test, Supplier<String> message) {
        if (test) {
            throw new UnsupportedDataSourcePropertyException(message.get());
        }
    }
}
