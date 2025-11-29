package org.springframework.scheduling;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/SchedulingAwareRunnable.class */
public interface SchedulingAwareRunnable extends Runnable {
    boolean isLongLived();
}
