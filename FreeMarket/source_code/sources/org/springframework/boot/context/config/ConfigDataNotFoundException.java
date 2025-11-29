package org.springframework.boot.context.config;

import org.springframework.boot.origin.OriginProvider;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataNotFoundException.class */
public abstract class ConfigDataNotFoundException extends ConfigDataException implements OriginProvider {
    public abstract String getReferenceDescription();

    ConfigDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
