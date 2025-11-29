package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ConstantLong.class */
public final class ConstantLong extends Constant {
    private final long bytes;

    ConstantLong(DataInput file) throws IOException {
        super((byte) 5);
        this.bytes = file.readLong();
    }

    public long getBytes() {
        return this.bytes;
    }
}
