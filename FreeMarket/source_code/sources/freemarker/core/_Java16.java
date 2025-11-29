package freemarker.core;

import java.lang.reflect.Method;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_Java16.class */
public interface _Java16 {
    boolean isRecord(Class<?> cls);

    Set<Method> getComponentAccessors(Class<?> cls);
}
