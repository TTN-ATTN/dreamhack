package org.apache.tomcat;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/JarScanFilter.class */
public interface JarScanFilter {
    boolean check(JarScanType jarScanType, String str);

    default boolean isSkipAll() {
        return false;
    }
}
