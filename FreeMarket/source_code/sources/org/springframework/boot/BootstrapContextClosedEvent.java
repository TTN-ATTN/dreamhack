package org.springframework.boot;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/BootstrapContextClosedEvent.class */
public class BootstrapContextClosedEvent extends ApplicationEvent {
    private final ConfigurableApplicationContext applicationContext;

    BootstrapContextClosedEvent(BootstrapContext source, ConfigurableApplicationContext applicationContext) {
        super(source);
        this.applicationContext = applicationContext;
    }

    public BootstrapContext getBootstrapContext() {
        return (BootstrapContext) this.source;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
