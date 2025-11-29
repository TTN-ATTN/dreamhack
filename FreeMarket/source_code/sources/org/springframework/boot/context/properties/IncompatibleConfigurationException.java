package org.springframework.boot.context.properties;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/IncompatibleConfigurationException.class */
public class IncompatibleConfigurationException extends RuntimeException {
    private final List<String> incompatibleKeys;

    public IncompatibleConfigurationException(String... incompatibleKeys) {
        super("The following configuration properties have incompatible values: " + Arrays.toString(incompatibleKeys));
        this.incompatibleKeys = Arrays.asList(incompatibleKeys);
    }

    public Collection<String> getIncompatibleKeys() {
        return this.incompatibleKeys;
    }
}
