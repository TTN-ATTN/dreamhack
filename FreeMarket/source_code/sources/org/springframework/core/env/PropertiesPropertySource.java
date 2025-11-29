package org.springframework.core.env;

import java.util.Map;
import java.util.Properties;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/PropertiesPropertySource.class */
public class PropertiesPropertySource extends MapPropertySource {
    public PropertiesPropertySource(String name, Properties source) {
        super(name, source);
    }

    protected PropertiesPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override // org.springframework.core.env.MapPropertySource, org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        String[] propertyNames;
        synchronized (((Map) this.source)) {
            propertyNames = super.getPropertyNames();
        }
        return propertyNames;
    }
}
