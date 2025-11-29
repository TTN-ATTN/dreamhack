package org.apache.logging.log4j.util;

import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.util.PropertySource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/PropertiesPropertySource.class */
public class PropertiesPropertySource implements PropertySource {
    private static final String PREFIX = "log4j2.";
    private final Properties properties;

    public PropertiesPropertySource(final Properties properties) {
        this.properties = properties;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return 0;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(final BiConsumer<String, String> action) {
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            action.accept((String) entry.getKey(), (String) entry.getValue());
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
        return PREFIX + ((Object) PropertySource.Util.joinAsCamelCase(tokens));
    }
}
