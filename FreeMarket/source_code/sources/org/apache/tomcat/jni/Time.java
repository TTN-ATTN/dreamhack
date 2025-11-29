package org.apache.tomcat.jni;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/Time.class */
public class Time {
    public static final long APR_USEC_PER_SEC = 1000000;
    public static final long APR_MSEC_PER_USEC = 1000;

    public static native long now();

    public static native String rfc822(long j);

    public static native String ctime(long j);

    public static native void sleep(long j);

    public static long sec(long t) {
        return t / APR_USEC_PER_SEC;
    }

    public static long msec(long t) {
        return t / 1000;
    }
}
