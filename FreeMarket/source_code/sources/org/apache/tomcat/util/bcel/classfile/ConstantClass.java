package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ConstantClass.class */
public final class ConstantClass extends Constant {
    private final int nameIndex;

    ConstantClass(DataInput dataInput) throws IOException {
        super((byte) 7);
        this.nameIndex = dataInput.readUnsignedShort();
    }

    public int getNameIndex() {
        return this.nameIndex;
    }
}
