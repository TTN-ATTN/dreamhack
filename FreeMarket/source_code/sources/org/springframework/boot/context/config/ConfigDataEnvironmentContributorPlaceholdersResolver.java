package org.springframework.boot.context.config;

import org.springframework.boot.context.config.ConfigDataEnvironmentContributor;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.env.PropertySource;
import org.springframework.util.PropertyPlaceholderHelper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributorPlaceholdersResolver.class */
class ConfigDataEnvironmentContributorPlaceholdersResolver implements PlaceholdersResolver {
    private final Iterable<ConfigDataEnvironmentContributor> contributors;
    private final ConfigDataActivationContext activationContext;
    private final boolean failOnResolveFromInactiveContributor;
    private final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ":", true);
    private final ConfigDataEnvironmentContributor activeContributor;

    ConfigDataEnvironmentContributorPlaceholdersResolver(Iterable<ConfigDataEnvironmentContributor> contributors, ConfigDataActivationContext activationContext, ConfigDataEnvironmentContributor activeContributor, boolean failOnResolveFromInactiveContributor) {
        this.contributors = contributors;
        this.activationContext = activationContext;
        this.activeContributor = activeContributor;
        this.failOnResolveFromInactiveContributor = failOnResolveFromInactiveContributor;
    }

    @Override // org.springframework.boot.context.properties.bind.PlaceholdersResolver
    public Object resolvePlaceholders(Object value) {
        if (value instanceof String) {
            return this.helper.replacePlaceholders((String) value, this::resolvePlaceholder);
        }
        return value;
    }

    private String resolvePlaceholder(String placeholder) {
        Object result = null;
        for (ConfigDataEnvironmentContributor contributor : this.contributors) {
            PropertySource<?> propertySource = contributor.getPropertySource();
            Object value = propertySource != null ? propertySource.getProperty(placeholder) : null;
            if (value != null && !isActive(contributor)) {
                if (this.failOnResolveFromInactiveContributor) {
                    ConfigDataResource resource = contributor.getResource();
                    Origin origin = OriginLookup.getOrigin(propertySource, placeholder);
                    throw new InactiveConfigDataAccessException(propertySource, resource, placeholder, origin);
                }
                value = null;
            }
            result = result != null ? result : value;
        }
        if (result != null) {
            return String.valueOf(result);
        }
        return null;
    }

    private boolean isActive(ConfigDataEnvironmentContributor contributor) {
        if (contributor == this.activeContributor) {
            return true;
        }
        if (contributor.getKind() != ConfigDataEnvironmentContributor.Kind.UNBOUND_IMPORT) {
            return contributor.isActive(this.activationContext);
        }
        return contributor.withBoundProperties(this.contributors, this.activationContext).isActive(this.activationContext);
    }
}
