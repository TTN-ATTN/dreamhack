package org.apache.tomcat;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/ContextBind.class */
public interface ContextBind {
    ClassLoader bind(boolean z, ClassLoader classLoader);

    void unbind(boolean z, ClassLoader classLoader);
}
