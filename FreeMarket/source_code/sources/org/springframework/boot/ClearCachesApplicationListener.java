package org.springframework.boot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ClearCachesApplicationListener.class */
class ClearCachesApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    ClearCachesApplicationListener() {
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ContextRefreshedEvent event) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        ReflectionUtils.clearCache();
        clearClassLoaderCaches(Thread.currentThread().getContextClassLoader());
    }

    private void clearClassLoaderCaches(ClassLoader classLoader) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        if (classLoader == null) {
            return;
        }
        try {
            Method clearCacheMethod = classLoader.getClass().getDeclaredMethod("clearCache", new Class[0]);
            clearCacheMethod.invoke(classLoader, new Object[0]);
        } catch (Exception e) {
        }
        clearClassLoaderCaches(classLoader.getParent());
    }
}
