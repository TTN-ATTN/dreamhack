package org.springframework.boot.autoconfigure.r2dbc;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/r2dbc/MultipleConnectionPoolConfigurationsException.class */
class MultipleConnectionPoolConfigurationsException extends RuntimeException {
    MultipleConnectionPoolConfigurationsException() {
        super("R2DBC connection pooling configuration should be provided by either the spring.r2dbc.pool.* properties or the spring.r2dbc.url property but both have been used.");
    }
}
