package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/ext/Java7Handlers.class */
public abstract class Java7Handlers {
    private static final Java7Handlers IMPL;

    public abstract Class<?> getClassJavaNioFilePath();

    public abstract JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> cls);

    public abstract JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> cls);

    static {
        Java7Handlers impl = null;
        try {
            Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ext.Java7HandlersImpl");
            impl = (Java7Handlers) ClassUtil.createInstance(cls, false);
        } catch (Throwable th) {
        }
        IMPL = impl;
    }

    public static Java7Handlers instance() {
        return IMPL;
    }
}
