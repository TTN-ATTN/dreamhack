package org.apache.catalina;

import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/DistributedManager.class */
public interface DistributedManager {
    int getActiveSessionsFull();

    Set<String> getSessionIdsFull();
}
