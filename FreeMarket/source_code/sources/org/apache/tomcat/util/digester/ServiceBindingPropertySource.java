package org.apache.tomcat.util.digester;

import java.io.FilePermission;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.security.PermissionCheck;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/digester/ServiceBindingPropertySource.class */
public class ServiceBindingPropertySource implements IntrospectionUtils.SecurePropertySource {
    private static final String SERVICE_BINDING_ROOT_ENV_VAR = "SERVICE_BINDING_ROOT";

    @Override // org.apache.tomcat.util.IntrospectionUtils.PropertySource
    public String getProperty(String key) {
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.tomcat.util.IntrospectionUtils.SecurePropertySource
    public String getProperty(String key, ClassLoader classLoader) {
        if (classLoader instanceof PermissionCheck) {
            Permission p = new RuntimePermission("getenv.SERVICE_BINDING_ROOT", null);
            if (!((PermissionCheck) classLoader).check(p)) {
                return null;
            }
        }
        String serviceBindingRoot = System.getenv(SERVICE_BINDING_ROOT_ENV_VAR);
        if (serviceBindingRoot == null) {
            return null;
        }
        String[] parts = key.split("\\.");
        if (parts.length != 2) {
            return null;
        }
        Path path = Paths.get(serviceBindingRoot, parts[0], parts[1]);
        try {
            if (classLoader instanceof PermissionCheck) {
                Permission p2 = new FilePermission(path.toString(), "read");
                if (!((PermissionCheck) classLoader).check(p2)) {
                    return null;
                }
            }
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            return null;
        }
    }
}
