package org.apache.tomcat.util.bcel.classfile;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ClassParser.class */
public final class ClassParser {
    private static final int BUFSIZE = 8192;
    private final DataInput dataInputStream;
    private String className;
    private String superclassName;
    private int accessFlags;
    private String[] interfaceNames;
    private ConstantPool constantPool;
    private Annotations runtimeVisibleAnnotations;
    private List<Annotations> runtimeVisibleFieldOrMethodAnnotations;
    private static final String[] INTERFACES_EMPTY_ARRAY = new String[0];

    public ClassParser(InputStream inputStream) {
        this.dataInputStream = new DataInputStream(new BufferedInputStream(inputStream, 8192));
    }

    public JavaClass parse() throws ClassFormatException, IOException {
        readID();
        readVersion();
        readConstantPool();
        readClassInfo();
        readInterfaces();
        readFields();
        readMethods();
        readAttributes(false);
        return new JavaClass(this.className, this.superclassName, this.accessFlags, this.constantPool, this.interfaceNames, this.runtimeVisibleAnnotations, this.runtimeVisibleFieldOrMethodAnnotations);
    }

    private void readAttributes(boolean fieldOrMethod) throws ClassFormatException, IOException {
        int attributesCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < attributesCount; i++) {
            int name_index = this.dataInputStream.readUnsignedShort();
            ConstantUtf8 c = (ConstantUtf8) this.constantPool.getConstant(name_index, (byte) 1);
            String name = c.getBytes();
            int length = this.dataInputStream.readInt();
            if (name.equals("RuntimeVisibleAnnotations")) {
                if (fieldOrMethod) {
                    Annotations fieldOrMethodAnnotations = new Annotations(this.dataInputStream, this.constantPool);
                    if (this.runtimeVisibleFieldOrMethodAnnotations == null) {
                        this.runtimeVisibleFieldOrMethodAnnotations = new ArrayList();
                    }
                    this.runtimeVisibleFieldOrMethodAnnotations.add(fieldOrMethodAnnotations);
                } else {
                    if (this.runtimeVisibleAnnotations != null) {
                        throw new ClassFormatException("RuntimeVisibleAnnotations attribute is not allowed more than once in a class file");
                    }
                    this.runtimeVisibleAnnotations = new Annotations(this.dataInputStream, this.constantPool);
                }
            } else {
                Utility.skipFully(this.dataInputStream, length);
            }
        }
    }

    private void readClassInfo() throws ClassFormatException, IOException {
        this.accessFlags = this.dataInputStream.readUnsignedShort();
        if ((this.accessFlags & 512) != 0) {
            this.accessFlags |= 1024;
        }
        if ((this.accessFlags & 1024) != 0 && (this.accessFlags & 16) != 0) {
            throw new ClassFormatException("Class can't be both final and abstract");
        }
        int classNameIndex = this.dataInputStream.readUnsignedShort();
        this.className = Utility.getClassName(this.constantPool, classNameIndex);
        int superclass_name_index = this.dataInputStream.readUnsignedShort();
        if (superclass_name_index > 0) {
            this.superclassName = Utility.getClassName(this.constantPool, superclass_name_index);
        } else {
            this.superclassName = "java.lang.Object";
        }
    }

    private void readConstantPool() throws ClassFormatException, IOException {
        this.constantPool = new ConstantPool(this.dataInputStream);
    }

    private void readFields() throws ClassFormatException, IOException {
        int fieldsCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < fieldsCount; i++) {
            Utility.skipFully(this.dataInputStream, 6);
            readAttributes(true);
        }
    }

    private void readID() throws ClassFormatException, IOException {
        if (this.dataInputStream.readInt() != -889275714) {
            throw new ClassFormatException("It is not a Java .class file");
        }
    }

    private void readInterfaces() throws ClassFormatException, IOException {
        int interfacesCount = this.dataInputStream.readUnsignedShort();
        if (interfacesCount > 0) {
            this.interfaceNames = new String[interfacesCount];
            for (int i = 0; i < interfacesCount; i++) {
                int index = this.dataInputStream.readUnsignedShort();
                this.interfaceNames[i] = Utility.getClassName(this.constantPool, index);
            }
            return;
        }
        this.interfaceNames = INTERFACES_EMPTY_ARRAY;
    }

    private void readMethods() throws ClassFormatException, IOException {
        int methodsCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < methodsCount; i++) {
            Utility.skipFully(this.dataInputStream, 6);
            readAttributes(true);
        }
    }

    private void readVersion() throws ClassFormatException, IOException {
        Utility.skipFully(this.dataInputStream, 4);
    }
}
