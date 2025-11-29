package org.apache.tomcat.util.bcel.classfile;

import freemarker.core.FMParserConstants;
import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ElementValue.class */
public abstract class ElementValue {
    public static final byte STRING = 115;
    public static final byte ENUM_CONSTANT = 101;
    public static final byte CLASS = 99;
    public static final byte ANNOTATION = 64;
    public static final byte ARRAY = 91;
    public static final byte PRIMITIVE_INT = 73;
    public static final byte PRIMITIVE_BYTE = 66;
    public static final byte PRIMITIVE_CHAR = 67;
    public static final byte PRIMITIVE_DOUBLE = 68;
    public static final byte PRIMITIVE_FLOAT = 70;
    public static final byte PRIMITIVE_LONG = 74;
    public static final byte PRIMITIVE_SHORT = 83;
    public static final byte PRIMITIVE_BOOLEAN = 90;
    private final int type;
    private final ConstantPool cpool;

    public abstract String stringifyValue();

    public static ElementValue readElementValue(DataInput input, ConstantPool cpool) throws IOException {
        return readElementValue(input, cpool, 0);
    }

    public static ElementValue readElementValue(DataInput input, ConstantPool cpool, int arrayNesting) throws IOException {
        byte tag = input.readByte();
        switch (tag) {
            case 64:
                return new AnnotationElementValue(64, new AnnotationEntry(input, cpool), cpool);
            case 65:
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case FMParserConstants.TRIVIAL_FTL_HEADER /* 77 */:
            case FMParserConstants.UNKNOWN_DIRECTIVE /* 78 */:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 88:
            case Opcodes.DUP /* 89 */:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 100:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            default:
                throw new ClassFormatException("Unexpected element value kind in annotation: " + ((int) tag));
            case 66:
            case 67:
            case 68:
            case 70:
            case 73:
            case 74:
            case 83:
            case 90:
            case 115:
                return new SimpleElementValue(tag, input.readUnsignedShort(), cpool);
            case 91:
                int arrayNesting2 = arrayNesting + 1;
                if (arrayNesting2 > 255) {
                    throw new ClassFormatException(String.format("Arrays are only valid if they represent %,d or fewer dimensions.", Integer.valueOf(Const.MAX_ARRAY_DIMENSIONS)));
                }
                int numArrayVals = input.readUnsignedShort();
                ElementValue[] evalues = new ElementValue[numArrayVals];
                for (int j = 0; j < numArrayVals; j++) {
                    evalues[j] = readElementValue(input, cpool, arrayNesting2);
                }
                return new ArrayElementValue(91, evalues, cpool);
            case 99:
                return new ClassElementValue(99, input.readUnsignedShort(), cpool);
            case 101:
                input.readUnsignedShort();
                return new EnumElementValue(101, input.readUnsignedShort(), cpool);
        }
    }

    ElementValue(int type, ConstantPool cpool) {
        this.type = type;
        this.cpool = cpool;
    }

    final ConstantPool getConstantPool() {
        return this.cpool;
    }

    final int getType() {
        return this.type;
    }
}
