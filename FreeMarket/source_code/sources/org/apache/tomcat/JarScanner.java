package org.apache.tomcat;

import javax.servlet.ServletContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/JarScanner.class */
public interface JarScanner {
    void scan(JarScanType jarScanType, ServletContext servletContext, JarScannerCallback jarScannerCallback);

    JarScanFilter getJarScanFilter();

    void setJarScanFilter(JarScanFilter jarScanFilter);
}
