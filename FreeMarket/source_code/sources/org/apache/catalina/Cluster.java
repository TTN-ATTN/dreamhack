package org.apache.catalina;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/Cluster.class */
public interface Cluster extends Contained {
    String getClusterName();

    void setClusterName(String str);

    Manager createManager(String str);

    void registerManager(Manager manager);

    void removeManager(Manager manager);

    void backgroundProcess();
}
