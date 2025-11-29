package org.springframework.cglib.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/core/SpringNamingPolicy.class */
public class SpringNamingPolicy extends DefaultNamingPolicy {
    public static final SpringNamingPolicy INSTANCE = new SpringNamingPolicy();

    @Override // org.springframework.cglib.core.DefaultNamingPolicy
    protected String getTag() {
        return "BySpringCGLIB";
    }
}
