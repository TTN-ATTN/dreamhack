package org.springframework.boot.context.config;

import org.springframework.boot.origin.Origin;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataLocationNotFoundException.class */
public class ConfigDataLocationNotFoundException extends ConfigDataNotFoundException {
    private final ConfigDataLocation location;

    public ConfigDataLocationNotFoundException(ConfigDataLocation location) {
        this(location, null);
    }

    public ConfigDataLocationNotFoundException(ConfigDataLocation location, Throwable cause) {
        this(location, getMessage(location), cause);
    }

    public ConfigDataLocationNotFoundException(ConfigDataLocation location, String message, Throwable cause) {
        super(message, cause);
        Assert.notNull(location, "Location must not be null");
        this.location = location;
    }

    public ConfigDataLocation getLocation() {
        return this.location;
    }

    @Override // org.springframework.boot.origin.OriginProvider
    public Origin getOrigin() {
        return Origin.from(this.location);
    }

    @Override // org.springframework.boot.context.config.ConfigDataNotFoundException
    public String getReferenceDescription() {
        return getReferenceDescription(this.location);
    }

    private static String getMessage(ConfigDataLocation location) {
        return String.format("Config data %s cannot be found", getReferenceDescription(location));
    }

    private static String getReferenceDescription(ConfigDataLocation location) {
        return String.format("location '%s'", location);
    }
}
