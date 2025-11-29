package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/aopalliance/intercept/Joinpoint.class */
public interface Joinpoint {
    @Nullable
    Object proceed() throws Throwable;

    @Nullable
    Object getThis();

    @Nonnull
    AccessibleObject getStaticPart();
}
