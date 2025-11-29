package org.apache.tomcat.util.compat;

import java.security.AccessController;
import java.util.Locale;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/compat/JrePlatform.class */
public class JrePlatform {
    private static final String OS_NAME_PROPERTY = "os.name";
    public static final boolean IS_MAC_OS;
    public static final boolean IS_WINDOWS;

    static {
        String osName;
        if (System.getSecurityManager() == null) {
            osName = System.getProperty(OS_NAME_PROPERTY);
        } else {
            osName = (String) AccessController.doPrivileged(() -> {
                return System.getProperty(OS_NAME_PROPERTY);
            });
        }
        IS_MAC_OS = osName.toLowerCase(Locale.ENGLISH).startsWith("mac os x");
        IS_WINDOWS = osName.startsWith("Windows");
    }
}
