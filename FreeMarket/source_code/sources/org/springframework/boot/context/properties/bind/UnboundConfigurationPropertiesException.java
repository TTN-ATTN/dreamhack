package org.springframework.boot.context.properties.bind;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.source.ConfigurationProperty;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/bind/UnboundConfigurationPropertiesException.class */
public class UnboundConfigurationPropertiesException extends RuntimeException {
    private final Set<ConfigurationProperty> unboundProperties;

    public UnboundConfigurationPropertiesException(Set<ConfigurationProperty> unboundProperties) {
        super(buildMessage(unboundProperties));
        this.unboundProperties = Collections.unmodifiableSet(unboundProperties);
    }

    public Set<ConfigurationProperty> getUnboundProperties() {
        return this.unboundProperties;
    }

    private static String buildMessage(Set<ConfigurationProperty> unboundProperties) {
        StringBuilder builder = new StringBuilder();
        builder.append("The elements [");
        String message = (String) unboundProperties.stream().map(p -> {
            return p.getName().toString();
        }).collect(Collectors.joining(","));
        builder.append(message).append("] were left unbound.");
        return builder.toString();
    }
}
