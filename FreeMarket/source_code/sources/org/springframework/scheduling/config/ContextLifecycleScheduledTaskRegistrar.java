package org.springframework.scheduling.config;

import org.springframework.beans.factory.SmartInitializingSingleton;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/config/ContextLifecycleScheduledTaskRegistrar.class */
public class ContextLifecycleScheduledTaskRegistrar extends ScheduledTaskRegistrar implements SmartInitializingSingleton {
    @Override // org.springframework.scheduling.config.ScheduledTaskRegistrar, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        scheduleTasks();
    }
}
