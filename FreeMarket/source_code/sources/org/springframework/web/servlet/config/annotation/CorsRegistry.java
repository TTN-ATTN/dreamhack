package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/config/annotation/CorsRegistry.class */
public class CorsRegistry {
    private final List<CorsRegistration> registrations = new ArrayList();

    public CorsRegistration addMapping(String pathPattern) {
        CorsRegistration registration = new CorsRegistration(pathPattern);
        this.registrations.add(registration);
        return registration;
    }

    protected Map<String, CorsConfiguration> getCorsConfigurations() {
        Map<String, CorsConfiguration> configs = CollectionUtils.newLinkedHashMap(this.registrations.size());
        for (CorsRegistration registration : this.registrations) {
            configs.put(registration.getPathPattern(), registration.getCorsConfiguration());
        }
        return configs;
    }
}
