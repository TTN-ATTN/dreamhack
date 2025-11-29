package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/PropertyFilePropertySource.class */
public class PropertyFilePropertySource extends PropertiesPropertySource {
    public PropertyFilePropertySource(final String fileName) {
        super(loadPropertiesFile(fileName));
    }

    private static Properties loadPropertiesFile(final String fileName) throws IOException {
        Properties props = new Properties();
        for (URL url : LoaderUtil.findResources(fileName)) {
            try {
                InputStream in = url.openStream();
                Throwable th = null;
                try {
                    try {
                        props.load(in);
                        if (in != null) {
                            if (0 != 0) {
                                try {
                                    in.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                in.close();
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (in != null) {
                        if (th != null) {
                            try {
                                in.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            in.close();
                        }
                    }
                    throw th4;
                }
            } catch (IOException e) {
                LowLevelLogUtil.logException("Unable to read " + url, e);
            }
        }
        return props;
    }

    @Override // org.apache.logging.log4j.util.PropertiesPropertySource, org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return 0;
    }
}
