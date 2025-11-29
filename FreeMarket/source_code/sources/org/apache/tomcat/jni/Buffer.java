package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/jni/Buffer.class */
public class Buffer {
    @Deprecated
    public static native ByteBuffer malloc(int i);

    @Deprecated
    public static native ByteBuffer calloc(int i, int i2);

    @Deprecated
    public static native ByteBuffer palloc(long j, int i);

    @Deprecated
    public static native ByteBuffer pcalloc(long j, int i);

    @Deprecated
    public static native ByteBuffer create(long j, int i);

    @Deprecated
    public static native void free(ByteBuffer byteBuffer);

    public static native long address(ByteBuffer byteBuffer);

    @Deprecated
    public static native long size(ByteBuffer byteBuffer);
}
