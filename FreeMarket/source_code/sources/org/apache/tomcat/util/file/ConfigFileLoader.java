package org.apache.tomcat.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/file/ConfigFileLoader.class */
public class ConfigFileLoader {
    private static ConfigurationSource source;

    public static final ConfigurationSource getSource() {
        if (source == null) {
            return ConfigurationSource.DEFAULT;
        }
        return source;
    }

    public static final void setSource(ConfigurationSource source2) {
        if (source == null) {
            source = source2;
        }
    }

    private ConfigFileLoader() {
    }

    @Deprecated
    public static InputStream getInputStream(String location) throws IOException {
        return getSource().getResource(location).getInputStream();
    }

    @Deprecated
    public static URI getURI(String location) {
        return getSource().getURI(location);
    }
}
