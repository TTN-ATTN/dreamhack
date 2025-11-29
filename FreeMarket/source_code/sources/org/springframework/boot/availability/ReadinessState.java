package org.springframework.boot.availability;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/availability/ReadinessState.class */
public enum ReadinessState implements AvailabilityState {
    ACCEPTING_TRAFFIC,
    REFUSING_TRAFFIC
}
