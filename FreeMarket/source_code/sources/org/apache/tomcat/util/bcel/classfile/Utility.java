package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/Utility.class */
final class Utility {
    static String compactClassName(String str) {
        return str.replace('/', '.');
    }

    static String getClassName(ConstantPool constantPool, int index) throws ClassFormatException {
        Constant c = constantPool.getConstant(index, (byte) 7);
        int i = ((ConstantClass) c).getNameIndex();
        Constant c2 = constantPool.getConstant(i, (byte) 1);
        String name = ((ConstantUtf8) c2).getBytes();
        return compactClassName(name);
    }

    static void skipFully(DataInput file, int length) throws IOException {
        int total = file.skipBytes(length);
        if (total != length) {
            throw new EOFException();
        }
    }

    static void swallowAttribute(DataInput file) throws IOException {
        skipFully(file, 2);
        int length = file.readInt();
        skipFully(file, length);
    }

    private Utility() {
    }
}
