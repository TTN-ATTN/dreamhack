package org.springframework.core.metrics;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/metrics/ApplicationStartup.class */
public interface ApplicationStartup {
    public static final ApplicationStartup DEFAULT = new DefaultApplicationStartup();

    StartupStep start(String name);
}
