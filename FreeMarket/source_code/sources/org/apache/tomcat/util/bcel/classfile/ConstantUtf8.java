package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.Objects;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ConstantUtf8.class */
public final class ConstantUtf8 extends Constant {
    private final String value;

    static ConstantUtf8 getInstance(DataInput dataInput) throws IOException {
        return new ConstantUtf8(dataInput.readUTF());
    }

    private ConstantUtf8(String value) {
        super((byte) 1);
        this.value = (String) Objects.requireNonNull(value, "value");
    }

    public String getBytes() {
        return this.value;
    }
}
