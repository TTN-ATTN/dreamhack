package org.apache.tomcat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/InstanceManagerBindings.class */
public final class InstanceManagerBindings {
    private static final Map<ClassLoader, InstanceManager> bindings = new ConcurrentHashMap();

    public static void bind(ClassLoader classLoader, InstanceManager instanceManager) {
        bindings.put(classLoader, instanceManager);
    }

    public static void unbind(ClassLoader classLoader) {
        bindings.remove(classLoader);
    }

    public static InstanceManager get(ClassLoader classLoader) {
        return bindings.get(classLoader);
    }
}
