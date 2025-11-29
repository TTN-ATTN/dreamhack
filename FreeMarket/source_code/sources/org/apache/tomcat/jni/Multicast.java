package org.apache.tomcat.jni;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/Multicast.class */
public class Multicast {
    public static native int join(long j, long j2, long j3, long j4);

    public static native int leave(long j, long j2, long j3, long j4);

    public static native int hops(long j, int i);

    public static native int loopback(long j, boolean z);

    public static native int ointerface(long j, long j2);
}
