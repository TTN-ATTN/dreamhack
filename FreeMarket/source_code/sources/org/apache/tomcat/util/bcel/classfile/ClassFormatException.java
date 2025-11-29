package org.apache.tomcat.util.bcel.classfile;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/bcel/classfile/ClassFormatException.class */
public class ClassFormatException extends RuntimeException {
    private static final long serialVersionUID = 3243149520175287759L;

    public ClassFormatException() {
    }

    public ClassFormatException(String message) {
        super(message);
    }

    public ClassFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassFormatException(Throwable cause) {
        super(cause);
    }
}
