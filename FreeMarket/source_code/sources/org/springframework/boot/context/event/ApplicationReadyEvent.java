package org.springframework.boot.context.event;

import java.time.Duration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/event/ApplicationReadyEvent.class */
public class ApplicationReadyEvent extends SpringApplicationEvent {
    private final ConfigurableApplicationContext context;
    private final Duration timeTaken;

    @Deprecated
    public ApplicationReadyEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context) {
        this(application, args, context, null);
    }

    public ApplicationReadyEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context, Duration timeTaken) {
        super(application, args);
        this.context = context;
        this.timeTaken = timeTaken;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }

    public Duration getTimeTaken() {
        return this.timeTaken;
    }
}
