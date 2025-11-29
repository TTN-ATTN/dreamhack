package org.springframework.boot;

import java.time.Duration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationRunListener.class */
public interface SpringApplicationRunListener {
    default void starting(ConfigurableBootstrapContext bootstrapContext) {
    }

    default void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
    }

    default void contextPrepared(ConfigurableApplicationContext context) {
    }

    default void contextLoaded(ConfigurableApplicationContext context) {
    }

    default void started(ConfigurableApplicationContext context, Duration timeTaken) {
        started(context);
    }

    @Deprecated
    default void started(ConfigurableApplicationContext context) {
    }

    default void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        running(context);
    }

    @Deprecated
    default void running(ConfigurableApplicationContext context) {
    }

    default void failed(ConfigurableApplicationContext context, Throwable exception) {
    }
}
