package org.springframework.boot;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringBootExceptionReporter.class */
public interface SpringBootExceptionReporter {
    boolean reportException(Throwable failure);
}
