package org.apache.tomcat;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/InstanceManager.class */
public interface InstanceManager {
    Object newInstance(Class<?> cls) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, NamingException, IllegalArgumentException, InvocationTargetException;

    Object newInstance(String str) throws IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, SecurityException, NamingException, IllegalArgumentException, InvocationTargetException;

    Object newInstance(String str, ClassLoader classLoader) throws IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, SecurityException, NamingException, IllegalArgumentException, InvocationTargetException;

    void newInstance(Object obj) throws IllegalAccessException, NamingException, InvocationTargetException;

    void destroyInstance(Object obj) throws IllegalAccessException, InvocationTargetException;

    default void backgroundProcess() {
    }
}
