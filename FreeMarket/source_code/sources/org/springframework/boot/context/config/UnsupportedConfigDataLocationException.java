package org.springframework.boot.context.config;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/UnsupportedConfigDataLocationException.class */
public class UnsupportedConfigDataLocationException extends ConfigDataException {
    private final ConfigDataLocation location;

    UnsupportedConfigDataLocationException(ConfigDataLocation location) {
        super("Unsupported config data location '" + location + "'", null);
        this.location = location;
    }

    public ConfigDataLocation getLocation() {
        return this.location;
    }
}
