package org.apache.catalina;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/SessionIdGenerator.class */
public interface SessionIdGenerator {
    String getJvmRoute();

    void setJvmRoute(String str);

    int getSessionIdLength();

    void setSessionIdLength(int i);

    String generateSessionId();

    String generateSessionId(String str);
}
