package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ConstantInteger.class */
public final class ConstantInteger extends Constant {
    private final int bytes;

    ConstantInteger(DataInput file) throws IOException {
        super((byte) 3);
        this.bytes = file.readInt();
    }

    public int getBytes() {
        return this.bytes;
    }
}
