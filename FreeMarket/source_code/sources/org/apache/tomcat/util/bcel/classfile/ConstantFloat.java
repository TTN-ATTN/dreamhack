package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ConstantFloat.class */
public final class ConstantFloat extends Constant {
    private final float bytes;

    ConstantFloat(DataInput file) throws IOException {
        super((byte) 4);
        this.bytes = file.readFloat();
    }

    public float getBytes() {
        return this.bytes;
    }
}
