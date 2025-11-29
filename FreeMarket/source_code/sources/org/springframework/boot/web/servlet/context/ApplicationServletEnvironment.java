package org.springframework.boot.web.servlet.context;

import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.context.support.StandardServletEnvironment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/context/ApplicationServletEnvironment.class */
class ApplicationServletEnvironment extends StandardServletEnvironment {
    ApplicationServletEnvironment() {
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
