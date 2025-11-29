package org.apache.tomcat.websocket;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/BackgroundProcess.class */
public interface BackgroundProcess {
    void backgroundProcess();

    void setProcessPeriod(int i);

    int getProcessPeriod();
}
