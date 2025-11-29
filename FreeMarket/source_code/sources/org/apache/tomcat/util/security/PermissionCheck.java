package org.apache.tomcat.util.security;

import java.security.Permission;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/security/PermissionCheck.class */
public interface PermissionCheck {
    boolean check(Permission permission);
}
