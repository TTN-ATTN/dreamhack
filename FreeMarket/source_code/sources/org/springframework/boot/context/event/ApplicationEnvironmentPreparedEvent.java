package org.springframework.boot.context.event;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/event/ApplicationEnvironmentPreparedEvent.class */
public class ApplicationEnvironmentPreparedEvent extends SpringApplicationEvent {
    private final ConfigurableBootstrapContext bootstrapContext;
    private final ConfigurableEnvironment environment;

    public ApplicationEnvironmentPreparedEvent(ConfigurableBootstrapContext bootstrapContext, SpringApplication application, String[] args, ConfigurableEnvironment environment) {
        super(application, args);
        this.bootstrapContext = bootstrapContext;
        this.environment = environment;
    }

    public ConfigurableBootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
    }

    public ConfigurableEnvironment getEnvironment() {
        return this.environment;
    }
}
