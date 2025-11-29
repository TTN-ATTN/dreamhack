package org.springframework.boot;

import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ApplicationEnvironment.class */
class ApplicationEnvironment extends StandardEnvironment {
    ApplicationEnvironment() {
    }

    @Override // org.springframework.core.env.AbstractEnvironment
    protected String doGetActiveProfilesProperty() {
        return null;
    }

    @Override // org.springframework.core.env.AbstractEnvironment
    protected String doGetDefaultProfilesProperty() {
        return null;
    }

    @Override // org.springframework.core.env.AbstractEnvironment
    protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return ConfigurationPropertySources.createPropertyResolver(propertySources);
    }
}
