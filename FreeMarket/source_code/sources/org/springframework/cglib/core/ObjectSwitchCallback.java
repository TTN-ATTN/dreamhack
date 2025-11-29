package org.springframework.cglib.core;

import org.springframework.asm.Label;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/core/ObjectSwitchCallback.class */
public interface ObjectSwitchCallback {
    void processCase(Object obj, Label label) throws Exception;

    void processDefault() throws Exception;
}
