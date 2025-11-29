package org.springframework.util.backoff;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/backoff/BackOff.class */
public interface BackOff {
    BackOffExecution start();
}
