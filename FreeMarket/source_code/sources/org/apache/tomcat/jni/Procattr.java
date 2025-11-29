package org.apache.tomcat.jni;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/Procattr.class */
public class Procattr {
    public static native long create(long j) throws Error;

    public static native int ioSet(long j, int i, int i2, int i3);

    public static native int childInSet(long j, long j2, long j3);

    public static native int childOutSet(long j, long j2, long j3);

    public static native int childErrSet(long j, long j2, long j3);

    public static native int dirSet(long j, String str);

    public static native int cmdtypeSet(long j, int i);

    public static native int detachSet(long j, int i);

    public static native int errorCheckSet(long j, int i);

    public static native int addrspaceSet(long j, int i);

    public static native void errfnSet(long j, long j2, Object obj);

    public static native int userSet(long j, String str, String str2);

    public static native int groupSet(long j, String str);
}
