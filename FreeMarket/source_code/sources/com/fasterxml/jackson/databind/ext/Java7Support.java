package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.util.ClassUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/ext/Java7Support.class */
public abstract class Java7Support {
    private static final Java7Support IMPL;

    public abstract Boolean findTransient(Annotated annotated);

    public abstract Boolean hasCreatorAnnotation(Annotated annotated);

    public abstract PropertyName findConstructorName(AnnotatedParameter annotatedParameter);

    static {
        Java7Support impl = null;
        try {
            Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ext.Java7SupportImpl");
            impl = (Java7Support) ClassUtil.createInstance(cls, false);
        } catch (Throwable th) {
        }
        IMPL = impl;
    }

    public static Java7Support instance() {
        return IMPL;
    }
}
