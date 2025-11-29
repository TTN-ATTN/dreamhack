package org.apache.tomcat.util.bcel.classfile;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/EnumElementValue.class */
public class EnumElementValue extends ElementValue {
    private final int valueIdx;

    EnumElementValue(int type, int valueIdx, ConstantPool cpool) {
        super(type, cpool);
        if (type != 101) {
            throw new ClassFormatException("Only element values of type enum can be built with this ctor - type specified: " + type);
        }
        this.valueIdx = valueIdx;
    }

    @Override // org.apache.tomcat.util.bcel.classfile.ElementValue
    public String stringifyValue() {
        return super.getConstantPool().getConstantUtf8(this.valueIdx).getBytes();
    }
}
