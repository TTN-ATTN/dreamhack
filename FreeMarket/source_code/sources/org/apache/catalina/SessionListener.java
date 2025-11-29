package org.apache.catalina;

import java.util.EventListener;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/SessionListener.class */
public interface SessionListener extends EventListener {
    void sessionEvent(SessionEvent sessionEvent);
}
