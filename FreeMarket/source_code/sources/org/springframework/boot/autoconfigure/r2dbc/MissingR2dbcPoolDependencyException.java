package org.springframework.boot.autoconfigure.r2dbc;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/r2dbc/MissingR2dbcPoolDependencyException.class */
class MissingR2dbcPoolDependencyException extends RuntimeException {
    MissingR2dbcPoolDependencyException() {
        super("R2DBC connection pooling has been configured but the io.r2dbc.pool.ConnectionPool class is not present.");
    }
}
