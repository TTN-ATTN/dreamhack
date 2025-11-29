package org.springframework.cglib.transform.impl;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/transform/impl/InterceptFieldEnabled.class */
public interface InterceptFieldEnabled {
    void setInterceptFieldCallback(InterceptFieldCallback interceptFieldCallback);

    InterceptFieldCallback getInterceptFieldCallback();
}
