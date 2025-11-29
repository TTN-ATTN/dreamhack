package org.springframework.context;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/LifecycleProcessor.class */
public interface LifecycleProcessor extends Lifecycle {
    void onRefresh();

    void onClose();
}
