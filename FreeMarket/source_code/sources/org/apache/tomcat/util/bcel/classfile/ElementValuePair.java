package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ElementValuePair.class */
public class ElementValuePair {
    private final ElementValue elementValue;
    private final ConstantPool constantPool;
    private final int elementNameIndex;

    ElementValuePair(DataInput file, ConstantPool constantPool) throws IOException {
        this.constantPool = constantPool;
        this.elementNameIndex = file.readUnsignedShort();
        this.elementValue = ElementValue.readElementValue(file, constantPool);
    }

    public String getNameString() {
        ConstantUtf8 c = (ConstantUtf8) this.constantPool.getConstant(this.elementNameIndex, (byte) 1);
        return c.getBytes();
    }

    public final ElementValue getValue() {
        return this.elementValue;
    }
}
