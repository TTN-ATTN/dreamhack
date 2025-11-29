package org.apache.tomcat.jni;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/Stdlib.class */
public class Stdlib {
    public static native boolean memread(byte[] bArr, long j, int i);

    public static native boolean memwrite(long j, byte[] bArr, int i);

    public static native boolean memset(long j, int i, int i2);

    public static native long malloc(int i);

    public static native long realloc(long j, int i);

    public static native long calloc(int i, int i2);

    public static native void free(long j);

    public static native int getpid();

    public static native int getppid();
}
