package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/core/FieldTypeCustomizer.class */
public interface FieldTypeCustomizer extends KeyFactoryCustomizer {
    void customize(CodeEmitter codeEmitter, int i, Type type);

    Type getOutType(int i, Type type);
}
