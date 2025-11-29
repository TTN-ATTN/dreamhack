package org.apache.tomcat.util.digester;

import java.security.Permission;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.security.PermissionCheck;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/digester/EnvironmentPropertySource.class */
public class EnvironmentPropertySource implements IntrospectionUtils.SecurePropertySource {
    @Override // org.apache.tomcat.util.IntrospectionUtils.PropertySource
    public String getProperty(String key) {
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.tomcat.util.IntrospectionUtils.SecurePropertySource
    public String getProperty(String key, ClassLoader classLoader) {
        if (classLoader instanceof PermissionCheck) {
            Permission p = new RuntimePermission("getenv." + key, null);
            if (!((PermissionCheck) classLoader).check(p)) {
                return null;
            }
        }
        return System.getenv(key);
    }
}
