package org.springframework.boot;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationShutdownHandlers.class */
public interface SpringApplicationShutdownHandlers {
    void add(Runnable action);

    void remove(Runnable action);
}
