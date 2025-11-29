package org.apache.tomcat.util.threads;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/threads/Constants.class */
public final class Constants {
    public static final long DEFAULT_THREAD_RENEWAL_DELAY = 1000;
    public static final boolean IS_SECURITY_ENABLED;

    static {
        IS_SECURITY_ENABLED = System.getSecurityManager() != null;
    }
}
