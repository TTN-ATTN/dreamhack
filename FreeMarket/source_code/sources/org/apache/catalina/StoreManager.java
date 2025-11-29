package org.apache.catalina;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/StoreManager.class */
public interface StoreManager extends DistributedManager {
    Store getStore();

    void removeSuper(Session session);
}
