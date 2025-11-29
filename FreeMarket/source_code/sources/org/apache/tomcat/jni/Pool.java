package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/Pool.class */
public class Pool {
    public static native long create(long j);

    @Deprecated
    public static native void clear(long j);

    public static native void destroy(long j);

    @Deprecated
    public static native long parentGet(long j);

    @Deprecated
    public static native boolean isAncestor(long j, long j2);

    @Deprecated
    public static native long cleanupRegister(long j, Object obj);

    @Deprecated
    public static native void cleanupKill(long j, long j2);

    @Deprecated
    public static native void noteSubprocess(long j, long j2, int i);

    @Deprecated
    public static native ByteBuffer alloc(long j, int i);

    @Deprecated
    public static native ByteBuffer calloc(long j, int i);

    @Deprecated
    public static native int dataSet(long j, String str, Object obj);

    @Deprecated
    public static native Object dataGet(long j, String str);

    @Deprecated
    public static native void cleanupForExec();
}
