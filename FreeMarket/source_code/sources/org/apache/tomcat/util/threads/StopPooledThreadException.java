package org.apache.tomcat.util.threads;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/threads/StopPooledThreadException.class */
public class StopPooledThreadException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public StopPooledThreadException(String msg) {
        super(msg, null, false, false);
    }
}
