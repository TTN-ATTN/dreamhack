package org.springframework.boot.context.config;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataResource.class */
public abstract class ConfigDataResource {
    private final boolean optional;

    public ConfigDataResource() {
        this(false);
    }

    protected ConfigDataResource(boolean optional) {
        this.optional = optional;
    }

    boolean isOptional() {
        return this.optional;
    }
}
