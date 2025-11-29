package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/config/SingletonBeanRegistry.class */
public interface SingletonBeanRegistry {
    void registerSingleton(String str, Object obj);

    @Nullable
    Object getSingleton(String str);

    boolean containsSingleton(String str);

    String[] getSingletonNames();

    int getSingletonCount();

    Object getSingletonMutex();
}
