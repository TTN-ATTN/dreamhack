package org.springframework.cglib.transform.impl;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/transform/impl/FieldProvider.class */
public interface FieldProvider {
    String[] getFieldNames();

    Class[] getFieldTypes();

    void setField(int i, Object obj);

    Object getField(int i);

    void setField(String str, Object obj);

    Object getField(String str);
}
