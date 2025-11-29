package org.apache.tomcat.jni;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/SSLSocket.class */
public class SSLSocket {
    public static native int attach(long j, long j2) throws Exception;

    public static native int handshake(long j);

    public static native int renegotiate(long j);

    public static native void setVerify(long j, int i, int i2);

    public static native byte[] getInfoB(long j, int i) throws Exception;

    public static native String getInfoS(long j, int i) throws Exception;

    public static native int getInfoI(long j, int i) throws Exception;

    public static native int getALPN(long j, byte[] bArr);
}
