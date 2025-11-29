package org.springframework.expression.spel.ast;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/spel/ast/TypeCode.class */
public enum TypeCode {
    OBJECT(Object.class),
    BOOLEAN(Boolean.TYPE),
    BYTE(Byte.TYPE),
    CHAR(Character.TYPE),
    DOUBLE(Double.TYPE),
    FLOAT(Float.TYPE),
    INT(Integer.TYPE),
    LONG(Long.TYPE),
    SHORT(Short.TYPE);

    private Class<?> type;

    TypeCode(Class type) {
        this.type = type;
    }

    public Class<?> getType() {
        return this.type;
    }

    public static TypeCode forName(String name) {
        TypeCode[] tcs = values();
        for (int i = 1; i < tcs.length; i++) {
            if (tcs[i].name().equalsIgnoreCase(name)) {
                return tcs[i];
            }
        }
        return OBJECT;
    }

    public static TypeCode forClass(Class<?> clazz) {
        TypeCode[] allValues = values();
        for (TypeCode typeCode : allValues) {
            if (clazz == typeCode.getType()) {
                return typeCode;
            }
        }
        return OBJECT;
    }
}
