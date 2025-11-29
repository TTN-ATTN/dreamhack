package org.springframework.core.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.springframework.core.SpringProperties;
import org.springframework.util.DefaultPropertiesPersister;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/support/ResourcePropertiesPersister.class */
public class ResourcePropertiesPersister extends DefaultPropertiesPersister {
    public static final ResourcePropertiesPersister INSTANCE = new ResourcePropertiesPersister();
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");

    @Override // org.springframework.util.DefaultPropertiesPersister, org.springframework.util.PropertiesPersister
    public void loadFromXml(Properties props, InputStream is) throws IOException {
        if (shouldIgnoreXml) {
            throw new UnsupportedOperationException("XML support disabled");
        }
        super.loadFromXml(props, is);
    }

    @Override // org.springframework.util.DefaultPropertiesPersister, org.springframework.util.PropertiesPersister
    public void storeToXml(Properties props, OutputStream os, String header) throws IOException {
        if (shouldIgnoreXml) {
            throw new UnsupportedOperationException("XML support disabled");
        }
        super.storeToXml(props, os, header);
    }

    @Override // org.springframework.util.DefaultPropertiesPersister, org.springframework.util.PropertiesPersister
    public void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException {
        if (shouldIgnoreXml) {
            throw new UnsupportedOperationException("XML support disabled");
        }
        super.storeToXml(props, os, header, encoding);
    }
}
