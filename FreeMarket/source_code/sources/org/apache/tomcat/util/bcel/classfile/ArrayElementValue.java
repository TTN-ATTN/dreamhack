package org.apache.tomcat.util.bcel.classfile;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ArrayElementValue.class */
public class ArrayElementValue extends ElementValue {
    private final ElementValue[] elementValues;

    ArrayElementValue(int type, ElementValue[] datums, ConstantPool cpool) {
        super(type, cpool);
        if (type != 91) {
            throw new ClassFormatException("Only element values of type array can be built with this ctor - type specified: " + type);
        }
        this.elementValues = datums;
    }

    public ElementValue[] getElementValuesArray() {
        return this.elementValues;
    }

    @Override // org.apache.tomcat.util.bcel.classfile.ElementValue
    public String stringifyValue() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < this.elementValues.length; i++) {
            sb.append(this.elementValues[i].stringifyValue());
            if (i + 1 < this.elementValues.length) {
                sb.append(',');
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
