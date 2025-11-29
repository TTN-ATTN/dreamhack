package org.apache.tomcat.jni;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/BIOCallback.class */
public interface BIOCallback {
    int write(byte[] bArr);

    int read(byte[] bArr);

    int puts(String str);

    String gets(int i);
}
