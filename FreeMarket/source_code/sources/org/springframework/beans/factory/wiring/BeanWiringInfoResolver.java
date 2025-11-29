package org.springframework.beans.factory.wiring;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/wiring/BeanWiringInfoResolver.class */
public interface BeanWiringInfoResolver {
    @Nullable
    BeanWiringInfo resolveWiringInfo(Object obj);
}
